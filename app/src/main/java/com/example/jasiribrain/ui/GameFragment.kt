package com.example.jasiribrain.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.jasiribrain.R
import com.example.jasiribrain.bluetooth.BluetoothController
import com.example.jasiribrain.data.Constants
import com.example.jasiribrain.data.JasiriDataHolder
import com.example.jasiribrain.data.JasiriViewModel
import com.example.jasiribrain.databinding.FragmentGameBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class GameFragment: Fragment() {

    private var _binding: FragmentGameBinding? = null
    private val binding get() = _binding!!

    private val viewModel: JasiriViewModel by viewModels()
    @Inject lateinit var controller: BluetoothController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            reactionGameDescription.visibility = View.VISIBLE
            reactionGameStartButton.visibility = View.VISIBLE
            gameFragmentExitBtn.visibility = View.VISIBLE
            gameStartedWait.visibility = View.INVISIBLE
            yourReactionTimeTitle.visibility = View.INVISIBLE
            reactionTiming.visibility = View.INVISIBLE
        }

        gameExitBtnInit()
        startGameBtnInit()
        userHasReacted()
        playAgainBtnInit()
    }

    private fun gameExitBtnInit() {
        binding.gameFragmentExitBtn.setOnClickListener {
            parentFragmentManager.beginTransaction().remove(this).commitNow()
        }
    }

    private fun startGameBtnInit() {
        binding.reactionGameStartButton.setOnClickListener {
            binding.run {
                reactionGameDescription.visibility = View.INVISIBLE
                reactionGameStartButton.visibility = View.INVISIBLE
                gameFragmentExitBtn.visibility = View.INVISIBLE
                gameStartedWait.visibility = View.VISIBLE
            }
            controller.sendMessage(Constants.GAME)
        }
    }

    private fun userHasReacted() {
        viewModel.hasUserReactedStatus.observe(viewLifecycleOwner) { reacted ->
            if (reacted) {
                binding.run {
                    gameStartedWait.visibility = View.INVISIBLE
                    yourReactionTimeTitle.visibility = View.VISIBLE
                }
                JasiriDataHolder.setHasuserReacted(false)
            }
        }

        viewModel.updateReactionTiming.observe(viewLifecycleOwner) { timing ->
            if (timing == "0") return@observe

            with(binding) {
                reactionTiming.visibility = View.VISIBLE
                if (timing == "f") {
                    gameStartedWait.visibility = View.INVISIBLE
                    yourReactionTimeTitle.visibility = View.VISIBLE
                    reactionTiming.text = getString(R.string.reaction_failed)
                } else {
                    reactionTiming.text = "$timing ms"
                }
                reactionTimePlayAgainButton.visibility = View.VISIBLE
                gameFragmentExitBtn.visibility = View.VISIBLE
            }
            JasiriDataHolder.setReactionTiming("0")
        }
    }

    private fun playAgainBtnInit() {
        with(binding) {
            reactionTimePlayAgainButton.setOnClickListener {
                yourReactionTimeTitle.visibility = View.INVISIBLE
                reactionTiming.visibility = View.INVISIBLE
                reactionTimePlayAgainButton.visibility = View.INVISIBLE
                reactionGameDescription.visibility = View.VISIBLE
                reactionGameStartButton.visibility = View.VISIBLE
            }
        }
    }


}