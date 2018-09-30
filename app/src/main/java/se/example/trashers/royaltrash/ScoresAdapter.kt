package se.example.trashers.royaltrash

import android.support.design.card.MaterialCardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import org.w3c.dom.Text

class ScoresAdapter(private val scores: ArrayList<ScoreBoardActivity.Score>) :
        RecyclerView.Adapter<ScoresAdapter.MyViewHolder>() {

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case that is shown in a TextView.
    class MyViewHolder(val score: MaterialCardView) : RecyclerView.ViewHolder(score)


    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): ScoresAdapter.MyViewHolder {
        // create a new view
        val ScoreCard = LayoutInflater.from(parent.context)
                .inflate(R.layout.score_item, parent, false) as MaterialCardView
        // set the view's size, margins, paddings and layout parameters
        return MyViewHolder(ScoreCard)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        when(position){
            0 -> holder.score.findViewById<ImageView>(R.id.score_img).setImageResource(R.drawable.trashy_1st)
            1 -> holder.score.findViewById<ImageView>(R.id.score_img).setImageResource(R.drawable.trashy_2nd)
            2 -> holder.score.findViewById<ImageView>(R.id.score_img).setImageResource(R.drawable.trashy_3d)
        }
        holder.score.findViewById<TextView>(R.id.score_count).text = scores.get(position).score.toString() + "p"
        holder.score.findViewById<TextView>(R.id.textContainer).text = scores.get(position).name
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = scores.size
}