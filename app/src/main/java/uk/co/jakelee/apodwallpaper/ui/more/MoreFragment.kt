package uk.co.jakelee.apodwallpaper.ui.more

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import uk.co.jakelee.apodwallpaper.R

class MoreFragment : Fragment() {

  private lateinit var moreViewModel: MoreViewModel

  override fun onCreateView(
      inflater: LayoutInflater,
      container: ViewGroup?,
      savedInstanceState: Bundle?
  ): View? {
    moreViewModel =
        ViewModelProviders.of(this).get(MoreViewModel::class.java)
    val root = inflater.inflate(R.layout.fragment_more, container, false)
    val textView: TextView = root.findViewById(R.id.text_more)
    moreViewModel.text.observe(viewLifecycleOwner, Observer {
      textView.text = it
    })
    return root
  }
}