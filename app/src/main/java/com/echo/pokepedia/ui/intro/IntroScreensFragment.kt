package com.echo.pokepedia.ui.intro

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.echo.pokepedia.R
import com.echo.pokepedia.data.preferences.SettingsDataStore
import com.echo.pokepedia.databinding.FragmentIntroScreensBinding
import com.echo.pokepedia.ui.BaseFragment
import kotlinx.coroutines.launch


class IntroScreensFragment : BaseFragment() {

    private var _binding: FragmentIntroScreensBinding? = null
    private val binding get() = _binding!!

    private val viewModel: IntroScreensViewModel by viewModels()

    private val introScreensAdapter = IntroScreensAdapter()

    private lateinit var settingsDataStore: SettingsDataStore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentIntroScreensBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setViewPagerAdapter()

        setDotsIndicator()

        onButtonClick()

        observeIsLastScreen()

        onPageChangedListener()

        setPreferencesDataStore()

        observeIsOnboardingAvailable()
    }

    private fun setViewPagerAdapter() {
        binding.viewPager.adapter = introScreensAdapter
    }

    private fun setDotsIndicator() {
        val dotsIndicator = binding.viewPagerDotsIndicator
        dotsIndicator.attachTo(binding.viewPager)
    }

    private fun onButtonClick() {
        with(binding) {
            buttonViewPager.setOnClickListener {
                editIsOnBoardingAvailable()
                val action = IntroScreensFragmentDirections.viewPagerFragmentToLoginFragment()
                findNavController().navigate(action)
            }
        }
    }

    private fun editIsOnBoardingAvailable() {
        val isOnboardingAvailable = false
        lifecycleScope.launch {
            settingsDataStore.saveOnboardingToPreferencesStore(isOnboardingAvailable, requireContext())
        }
    }

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
            setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.blue_pokemon))
            setTextColor(ContextCompat.getColor(requireContext(), R.color.yellow_pokemon))
        }
    }

    private fun setButtonStyleAndTextOnOtherScreen() {
        with(binding.buttonViewPager) {
            text = resources.getText(R.string.skip)
            setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.transparent))
            setTextColor(ContextCompat.getColor(requireContext(), R.color.blue_pokemon))
        }
    }

    private fun onPageChangedListener() {
        binding.viewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                if (position == introScreensAdapter.itemCount - 1) {
                    viewModel.setIsLastScreen(true)
                } else {
                    viewModel.setIsLastScreen(false)
                }
            }
        })
    }

    private fun setPreferencesDataStore() {
        settingsDataStore = SettingsDataStore(requireContext())
    }

    private fun observeIsOnboardingAvailable() {
        settingsDataStore.onboardingPreferencesFlow.asLiveData().observe(viewLifecycleOwner) {isOnboardingAvailable ->
            if (!isOnboardingAvailable) {
                val action = IntroScreensFragmentDirections.viewPagerFragmentToLoginFragment()
                findNavController().navigate(action)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}