package se.example.trashers.royaltrash

import android.app.Activity
import android.content.*
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_login_dialog.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch




class LoginDialogFragment : DialogFragment(){

    /*
    *
    *
    *
    * DONT
    * MIND
    * THIS
    * FRAGMENT
    *
    *
    * */


    interface fragmentComunication {
        fun fragmentComunicationSetUsername(Username:String)
    }

    var delegate: fragmentComunication? = null

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
        if (context is fragmentComunication) {
            delegate = context
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_login_dialog, container, false)
        val btnAccept = view.findViewById<View>(R.id.buttonAccept) as Button
        //FontUtils.setTypeface(getActivity(), textViewQuestion, "fonts/mangal.ttf");
        //FontUtils.setTypeface(getActivity(), textViewAnswer, "fonts/mangal.ttf");
        btnAccept.setOnClickListener {
            funfun()
        }
        return view
    }

    fun funfun(){
        if (!username.text.isEmpty()){
            chekIfUserExists(username.text.toString())
        }
    }

    fun makeToast(Taost:String){
        activity!!.runOnUiThread { Toast.makeText(activity, Taost, Toast.LENGTH_SHORT).show() }
    }

    fun chekIfUserExists(Username:String){
        var Scores:Array<ScoreBoardActivity.Highscore>? = null
        var FoundUser =false
        val Username = Username.replace("[^A-Za-z0-9]+".toRegex(), "")
        statusbar.text = "working..."
        launch {
            Scores = DBrequests().apiGetHighscores()
            for (itm in Scores!!) {
                if (itm.hs_username == Username) {
                    FoundUser = true
                    break
                }
            }
            if(FoundUser){
                //Toast.makeText(activity, "Username taken!", Toast.LENGTH_SHORT).show()
                makeToast("Username alredy taken :(")
                launch(UI) {
                    statusbar.text = "username taken, try again"
                }
            }else{
                launch(UI) {
                    statusbar.text = "setting username.."
                }
                dismiss()
                //post the username
                val PostUser = hashMapOf("hs_username" to Username, "hs_score" to 0)
                val JsonStr = Gson().toJson(PostUser)
                DBrequests().apiHttpPostToServer("http://royaltrashapp.azurewebsites.net/api/highscores", JsonStr)
                delegate?.fragmentComunicationSetUsername(Username)
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