package br.com.chucknorris.global.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.chucknorris.R
import br.com.chucknorris.databinding.BottomSheetDialogFeedbackBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class FeedbackBottomSheetDialogFragment private constructor() : BottomSheetDialogFragment() {
    private var _dialogFeedbackBinding: BottomSheetDialogFeedbackBinding? = null
    private val dialogFeedbackBinding get() = _dialogFeedbackBinding!!

    private var onCloseCallback: (() -> Unit)? = null

    override fun getTheme(): Int = R.style.BottomSheetDialogThemeTransparent

    companion object {
        const val TAG = "FeedbackBottomSheetDialogFragment"

        private const val ARG_STATUS_ICON_RESOURCE_ID = "ARG_STATUS_ICON_RESOURCE_ID"
        private const val ARG_TITLE = "ARG_TITLE"
        private const val ARG_DESCRIPTION = "ARG_DESCRIPTION"
        private const val ARG_CLOSE_OPTION_LABEL = "ARG_CLOSE_OPTION_LABEL"

        fun newInstance(
            statusIconResourceId: Int,
            title: String,
            description: String?,
            closeOptionLabel: String,
            onCloseCallback: (() -> Unit)? = null
        ): FeedbackBottomSheetDialogFragment {

            val feedbackBottomSheetDialogFragment = FeedbackBottomSheetDialogFragment()

            val args = Bundle()

            args.putInt(ARG_STATUS_ICON_RESOURCE_ID, statusIconResourceId)
            args.putString(ARG_TITLE, title)
            args.putString(ARG_DESCRIPTION, description)
            args.putString(ARG_CLOSE_OPTION_LABEL, closeOptionLabel)

            feedbackBottomSheetDialogFragment.arguments = args
            feedbackBottomSheetDialogFragment.onCloseCallback = onCloseCallback

            return feedbackBottomSheetDialogFragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreate(savedInstanceState);
        _dialogFeedbackBinding =
            BottomSheetDialogFeedbackBinding.inflate(inflater, container, false)

        return dialogFeedbackBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let { args ->
            val statusIconResourceId = args.getInt(ARG_STATUS_ICON_RESOURCE_ID)
            val title = args.getString(ARG_TITLE)
            val description = args.getString(ARG_DESCRIPTION)
            val closeOptionLabel = args.getString(ARG_CLOSE_OPTION_LABEL)

            dialogFeedbackBinding.imageViewStatusIcon.setImageResource(statusIconResourceId)
            dialogFeedbackBinding.textViewTitle.text = title
            dialogFeedbackBinding.textViewMessage.text = description
            dialogFeedbackBinding.closeTextView.text = closeOptionLabel

            dialogFeedbackBinding.closeTextView.setOnClickListener {

                dismiss()

                onCloseCallback?.invoke()
            }
        }
    }
}
