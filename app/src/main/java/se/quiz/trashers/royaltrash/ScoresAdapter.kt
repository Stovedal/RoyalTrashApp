package se.quiz.trashers.royaltrash

import android.animation.Animator
import android.animation.ValueAnimator
import android.animation.ValueAnimator.INFINITE
import android.graphics.Color
import android.support.design.card.MaterialCardView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.TextView
import kotlinx.coroutines.experimental.NonCancellable.cancel
import kotlinx.coroutines.experimental.withTimeout
import org.w3c.dom.Text
import java.lang.Thread.sleep

class ScoresAdapter(private val scores: ArrayList<DBrequests.Highscore>, private val userPosition: Int) : RecyclerView.Adapter<ScoresAdapter.MyViewHolder>() {

    public val userPos = userPosition

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
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
            !in 0..2 -> {
                holder.score.findViewById<ImageView>(R.id.score_img).setImageResource(android.R.color.transparent)
                holder.score.findViewById<TextView>(R.id.score_position).text= (position+1).toString() + "."
            }
            0 -> holder.score.findViewById<ImageView>(R.id.score_img).setImageResource(R.drawable.trashy_1st)
            1 -> holder.score.findViewById<ImageView>(R.id.score_img).setImageResource(R.drawable.trashy_2nd)
            2 -> holder.score.findViewById<ImageView>(R.id.score_img).setImageResource(R.drawable.trashy_3d)
        }

        holder.score.findViewById<TextView>(R.id.score_count).text = scores.get(position).hs_score.toString() + "p"
        holder.score.findViewById<TextView>(R.id.textContainer).text = scores.get(position).hs_username


        if(position.equals(0)){
            val color = "#D6BD3E"
            holder.score.findViewById<TextView>(R.id.textContainer).setTextColor(Color.parseColor(color))
            holder.score.findViewById<TextView>(R.id.textContainer).setTextSize(30f)
            holder.score.findViewById<MaterialCardView>(R.id.score_container).setPadding(20,20,20,20)
            holder.score.findViewById<MaterialCardView>(R.id.score_container).radius = 150f

            holder.score.strokeColor = Color.parseColor(color)
            holder.score.strokeWidth = 10
            holder.score.findViewById<TextView>(R.id.leader).text = "LEDARE!"
            val valueAnimator = ValueAnimator.ofFloat(0f, -50f)
            valueAnimator.repeatMode = ValueAnimator.REVERSE
            valueAnimator.repeatCount = ValueAnimator.INFINITE
            valueAnimator.addUpdateListener {
                val value = it.animatedValue as Float
                if(scores.get(position).hs_score == 1337 && position == userPosition){
                    holder.score.findViewById<ImageView>(R.id.score_img).rotation = value
                } else {
                    holder.score.findViewById<ImageView>(R.id.score_img).translationY = value
                }

            }
            valueAnimator.interpolator = AccelerateInterpolator()
            valueAnimator.duration = 200
            valueAnimator.start()
        } else if(position.equals(userPosition)){
            val color = "#29CFAF"
            holder.score.findViewById<TextView>(R.id.textContainer).setTextColor(Color.parseColor(color))
            holder.score.findViewById<TextView>(R.id.leader).text = "DU!"
            holder.score.findViewById<TextView>(R.id.leader).setTextColor(Color.parseColor(color))
            holder.score.strokeColor = Color.parseColor(color)
            holder.score.strokeWidth = 10

        }
    }


    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = scores.size
}