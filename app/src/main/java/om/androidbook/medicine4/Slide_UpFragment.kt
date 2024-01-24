package om.androidbook.medicine4

import android.animation.AnimatorInflater
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment

class Slide_UpFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_calendar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //val slideUpAnimation = AnimatorInflater.loadAnimator(requireContext(), R.animator.slide_up_animation)
        //slideUpAnimation.setTarget(view?.findViewById(R.id.listToMove))

        //val schedulelistButton = view.findViewById<Button>(R.id.schedulelistButton)
        //schedulelistButton.setOnClickListener {
        //    slideUpAnimation.start()
        //    schedulelistButton.visibility = View.INVISIBLE
        //}
    }
}