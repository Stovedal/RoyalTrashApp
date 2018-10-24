package se.quiz.trashers.royaltrash

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView


class PauseDialogFragment : DialogFragment(){

    /*
    *
    * "I'll still be here tomorrow to high five you yesterday, my friend. Peace"
    *   - Adventure times
    *
    * */


    interface FragmentCommunication {
        fun fragmentCommunicationStart()
    }

    private var delegate: FragmentCommunication? = null

    private var content: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        content = arguments!!.getString("content")
        //getDialog().window.setBackgroundDrawableResource(android.R.color.transparent);
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



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.puase_dialog_layout, container, false)
        val btnAccept = view.findViewById<View>(R.id.startTrashTrowing) as Button
        val text = view.findViewById<View>(R.id.description) as TextView
        text.text = getString(R.string.trash_sort_help)
        dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        /* btnAccept.setOnClickListener {
            funfun()
        }*/

        view.setOnClickListener {
            funfun()
        }

        return view
    }

    private fun funfun(){

        delegate?.fragmentCommunicationStart()
        dismiss()
    }


    companion object {
        fun newInstance(content: String): PauseDialogFragment {
            val fragm = PauseDialogFragment()
            val args = Bundle()
            args.putString("content", content)
            fragm.arguments = args
            return fragm
        }
    }
}