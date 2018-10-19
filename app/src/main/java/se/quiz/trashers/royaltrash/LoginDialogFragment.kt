package se.quiz.trashers.royaltrash

import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_login_dialog.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch


class LoginDialogFragment : DialogFragment(){

    /*
    *
    * "Sucking at something is the first step towards being sorta good at something."
    *   - Adventure times
    *
    * */


    interface FragmentCommunication {
        fun fragmentCommunicationSetUsername(Username:String)
    }

    private var delegate: FragmentCommunication? = null

    private var content: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        content = arguments!!.getString("content")
        val style = DialogFragment.STYLE_NO_FRAME
        val theme = R.style.AppTheme
        setStyle(style, theme)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is FragmentCommunication) {
            delegate = context
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_login_dialog, container, false)
        val btnAccept = view.findViewById<View>(R.id.buttonAccept) as Button
        //FontUtils.setTypeface(getActivity(), textViewQuestion, "fonts/mangal.ttf");
        //FontUtils.setTypeface(getActivity(), textViewAnswer, "fonts/mangal.ttf");
        btnAccept.setOnClickListener {
            funfun()
        }
        return view
    }

    private fun funfun(){
        if (!username.text.isEmpty()){
            chekIfUserExists(username.text.toString())
        }
    }

    private fun makeToast(Taost:String){
        try {
            activity!!.runOnUiThread { Toast.makeText(activity, Taost, Toast.LENGTH_SHORT).show() }
        }catch (E:Exception){
            println("Accec denied to UI thread: " + E)
        }
    }

    private fun chekIfUserExists(Username:String){
        var scores:Array<DBrequests.Highscore>? = null
        var foundUser =false
        var username = Username.replace("[^A-ZÅÄÖa-zåäö0-9]+".toRegex(), "").toLowerCase()
        if(username.length > 10){
            username = username.substring(0,9)
        }
        statusbar.text = "working..."
        launch {

            try {
                //Scores = DBrequests().apiGetHighscores()
                scores = DBrequests().apiGetHighscoreByUsername(username)
            }catch (e: Exception){
                println("Printing scores: $scores")
                println("LoginDialogFragment ERROR in db connection (GET): $e")
            }
            if(scores != null) {
                try {
                    if (scores!![0].hs_username.toLowerCase() == username) {
                        foundUser = true
                    }
                }catch (E:Exception){
                    println("ERROR: " + E)
                }

            }else{
                try {
                    launch(UI) {
                        statusbar.text = "something went wrong.. check your internet connection"
                    }
                }catch (d: Exception){
                    println("WARNING! NO data from server!")
                }
            }
            if(foundUser){
                //Toast.makeText(activity, "Username taken!", Toast.LENGTH_SHORT).show()
                makeToast("Username alredy taken :(")
                try {
                    launch(UI) {
                        statusbar.text = "username taken, try again"
                    }
                }catch (f:Exception){
                    println("ERROR in UI launcher thread!: " + f)
                }
            }else{
                try{
                    launch(UI) {
                        statusbar.text = "setting username.."
                    }
                }catch (f:Exception){
                    println("ERROR in UI launcher thread!: $f")
                }

                //post the username
                val postUser = hashMapOf("hs_username" to username, "hs_score" to 0)
                val jsonStr = Gson().toJson(postUser)
                try {
                    DBrequests().apiHttpPostToServer(jsonStr)
                }catch (g: Exception){
                    println("ERROR in db connection (POST): $g")
                }
                dismiss()
                delegate?.fragmentCommunicationSetUsername(username)

            }
        }
    }
    companion object {
        fun newInstance(content: String): LoginDialogFragment {
            val fragm = LoginDialogFragment()
            val args = Bundle()
            args.putString("content", content)
            fragm.arguments = args
            return fragm
        }
    }
}