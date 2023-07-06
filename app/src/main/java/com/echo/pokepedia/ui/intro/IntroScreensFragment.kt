package com.echo.pokepedia.ui.intro

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.echo.pokepedia.R
import com.echo.pokepedia.databinding.FragmentIntroScreensBinding
import com.echo.pokepedia.ui.BaseFragment
import com.echo.pokepedia.util.getColorRes
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class IntroScreensFragment : BaseFragment<IntroScreensViewModel>() {

    // region fragment variables
    private var _binding: FragmentIntroScreensBinding? = null
    private val binding get() = _binding!!

    private val introScreensAdapter = IntroScreensAdapter()
    //endregion

    // region fragment methods
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentIntroScreensBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initObservers()

        initUI()

        initListeners()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun getViewModelClass(): Class<IntroScreensViewModel> =
        IntroScreensViewModel::class.java
    //endregion

    private fun initObservers() {
        observeIsLastScreen()
        observeIsOnBoardingAvailable()
    }

    private fun initUI() {
        setViewPagerAdapter()
        setDotsIndicator()
    }

    private fun initListeners() {
        onSkipOrDoneButtonClickListener()
        onPageChangedListener()
    }

    // region initObservers
    private fun observeIsLastScreen() {
        viewModel.isLastScreen.observe(viewLifecycleOwner) { isLastScreen ->
            if (isLastScreen) {
                setButtonStyleAndTextOnLastScreen()
            } else {
                setButtonStyleAndTextOnOtherScreen()
            }
        }
    }

    private fun setButtonStyleAndTextOnLastScreen() {
        with(binding.buttonViewPager) {
            text = resources.getText(R.string.done)
            setBackgroundColor(context.getColorRes(R.color.blue_pokemon))
            setTextColor(context.getColorRes(R.color.yellow_pokemon))
        }
    }

    private fun setButtonStyleAndTextOnOtherScreen() {
        with(binding.buttonViewPager) {
            text = resources.getText(R.string.skip)
            setBackgroundColor(context.getColorRes(R.color.transparent))
            setTextColor(context.getColorRes(R.color.blue_pokemon))
        }
    }

    private fun observeIsOnBoardingAvailable() {
        viewModel.isOnBoardingAvailable.observe(viewLifecycleOwner) { isOnBoardingAvailable ->
            if (!isOnBoardingAvailable) {
                val action = IntroScreensFragmentDirections.viewPagerFragmentToLoginActivity()
                findNavController().navigate(action)
                activity?.finish()
            }
        }
    }
    // endregion

    // region initUI
    private fun setViewPagerAdapter() {
        binding.viewPager.adapter = introScreensAdapter
    }

    private fun setDotsIndicator() {
        val dotsIndicator = binding.viewPagerDotsIndicator
        dotsIndicator.attachTo(binding.viewPager)
    }
    // endregion

    // region initListeners
    private fun onSkipOrDoneButtonClickListener() {
        with(binding) {
            buttonViewPager.setOnClickListener {
                editIsOnBoardingAvailable()
            }
        }
    }

    private fun editIsOnBoardingAvailable() {
        val isOnBoardingAvailable = false
        viewModel.setIsOnBoardingAvailable(isOnBoardingAvailable)
    }

    private fun onPageChangedListener() {
        binding.viewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                val isLastScreen = position == introScreensAdapter.itemCount - 1
                viewModel.setIsLastScreen(isLastScreen)
            }
        })
    }
    // endregion

}