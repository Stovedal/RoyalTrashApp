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







    private var content: String? = null

    var UsernameState = 0 //0 = not cheked, 1 = username is avaliable, 2 = username is taken

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        content = arguments!!.getString("content")

        // Pick a style based on the num.
        val style = DialogFragment.STYLE_NO_FRAME
        val theme = R.style.AppTheme
        setStyle(style, theme)
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_login_dialog, container, false)
        val btnCancel = view.findViewById<View>(R.id.buttonCancel) as Button
        val btnAccept = view.findViewById<View>(R.id.buttonAccept) as Button

        val textViewContent = view.findViewById<View>(R.id.textViewContent) as TextView
        textViewContent.text = content
        //FontUtils.setTypeface(getActivity(), textViewQuestion, "fonts/mangal.ttf");
        //FontUtils.setTypeface(getActivity(), textViewAnswer, "fonts/mangal.ttf");
        btnCancel.setOnClickListener {
            dismiss()
        }
        btnAccept.setOnClickListener {
            funfun()
        }
        return view
    }

    fun funfun(){

        if (!username.text.isEmpty()){
            println("Username: " + username.text.toString())
            chekIfUserExists(username.text.toString())
            //dismiss()
        }


    }
    fun makeToast(Taost:String){
        //activity.runOnUiThread()

        activity!!.runOnUiThread { Toast.makeText(activity, Taost, Toast.LENGTH_SHORT).show() }

        //Toast.makeText(activity, Taost, Toast.LENGTH_SHORT).show()
    }

    fun chekIfUserExists(Username:String){
        var Scores:Array<ScoreBoardActivity.Highscore>? = null
        var FoundUser =false

        launch {
            Scores = DBrequests().apiGetHighscores()
            for (itm in Scores!!)
                if(itm.hs_username == Username){
                    FoundUser=true
                    break
                }
            if(FoundUser){
                UsernameState = 2
                //Toast.makeText(activity, "Username taken!", Toast.LENGTH_SHORT).show()
                makeToast("Username alredy taken :(")
            }else{
                //Toast.makeText(activity, "Username free!", Toast.LENGTH_SHORT).show()
                makeToast("Username free!:)")
                dismiss()
                UsernameState = 1
            }
        }


    }



    companion object {

        /**
         * Create a new instance of CustomDialogFragment, providing "num" as an
         * argument.
         */
        fun newInstance(content: String): LoginDialogFragment {
            val f = LoginDialogFragment()

            // Supply num input as an argument.
            val args = Bundle()
            args.putString("content", content)
            f.arguments = args

            return f
        }
    }

}