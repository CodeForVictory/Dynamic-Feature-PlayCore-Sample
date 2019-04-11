package com.chewy.android.dynamicfeaturessample

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.play.core.splitinstall.*
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus
import kotlinx.android.synthetic.main.fragment_dynamic_feature_loading.*

internal fun dynamicFeatureLoadingFragment(): Fragment = DynamicFeatureLoadingFragment()

private const val NO_SESSION = -1

class DynamicFeatureLoadingFragment : Fragment() {

    private val classTag: String = this::class.java.simpleName
    private var currentSessionId: Int = NO_SESSION

    private val splitInstallManager: SplitInstallManager by lazy {
        SplitInstallManagerFactory.create(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_dynamic_feature_loading, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        open_feature.setOnClickListener {
            if (!available(Feature.TheMeg(requireContext()))) {
                Log.i("Fetch", "fetching all")

                dynamic_feature_loading_refresh.isRefreshing = true
                dynamic_feature_loading_error.hide()
                open_feature.hide()

                fetchAllFeatures()
            } else {
                Toast.makeText(requireContext(), "Feature available or running in locale", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun available(feature: Feature): Boolean = (playStoreAvailable(feature) || localAvailable(feature))

    private fun localAvailable(feature: Feature): Boolean = try {
        feature.dependencies.firstOrNull()?.let { existingDependency ->
            existingDependency.newInstance()
            true
        } ?: false
    } catch (e: Exception) {
        false
    }.also {
        Log.i(classTag, "localAvailable = $it")
    }

    private fun playStoreAvailable(feature: Feature): Boolean =
        splitInstallManager.installedModules.contains(feature.name).also {
            Log.i(classTag, "playStoreAvailable = $it")
        }

    fun fetchAllFeatures() {
        Log.i(classTag, "fetchAllFeatures() --> start")
        Log.i(classTag, "sessionId = $currentSessionId")

        val changesListener = SplitInstallStateUpdatedListener {
            Log.i(classTag, "splitInstallStateUpdate")
            currentSessionId = it.sessionId()
            Log.i(classTag, "sessionId = $currentSessionId")

            when (it.status()) {
                SplitInstallSessionStatus.UNKNOWN-> {
                    Log.i(classTag, "errorCode: ${it.errorCode()}")
                    Log.i(classTag, "status: UNKNOWN")
                }
                SplitInstallSessionStatus.PENDING -> {
                    Log.i(classTag, "errorCode: ${it.errorCode()}")
                    Log.i(classTag, "status: PENDING")
                }
                SplitInstallSessionStatus.REQUIRES_USER_CONFIRMATION -> {
                    Log.i(classTag, "errorCode: ${it.errorCode()}")
                    Log.i(classTag, "status: REQUIRES_USER_CONFIRMATION")
                }
                SplitInstallSessionStatus.DOWNLOADING -> {
                    Log.i(classTag, "errorCode: ${it.errorCode()}")
                    Log.i(classTag, "status: REQUIRES_USER_CONFIRMATION")
                }
                SplitInstallSessionStatus.DOWNLOADED -> {
                    Log.i(classTag, "errorCode: ${it.errorCode()}")
                    Log.i(classTag, "status: DOWNLOADED")
                }
                SplitInstallSessionStatus.INSTALLING -> {
                    Log.i(classTag, "errorCode: ${it.errorCode()}")
                    Log.i(classTag, "status: INSTALLING")
                }
                SplitInstallSessionStatus.INSTALLED -> {
                    // Updates the app’s context with the code and resources of the
                    // installed module. (should only be for instant apps but tried it anyway, no change)
                    SplitInstallHelper.updateAppInfo(context)
                    Log.i(classTag, "errorCode: ${it.errorCode()}")
                    Log.i(classTag, "status: INSTALLED")

                    installFeatures(allFeatures(requireContext()))
                    startActivity(Intent().apply {
                        setClassName(
                            requireContext().packageName,
                            "com.chewy.android.dynamicfeaturessample.themeg.DynamicFeatureActivity"
                        )
                    })

                    currentSessionId = NO_SESSION
                }
                SplitInstallSessionStatus.FAILED -> {
                    Log.i(classTag, "errorCode: ${it.errorCode()}")
                    Log.i(classTag, "status: FAILED")

                    Toast.makeText(requireContext(), "FAILED INSTALL: errorCode: ${it.errorCode()}", Toast.LENGTH_LONG)
                        .show()

                    dynamic_feature_loading_refresh.isRefreshing = false
                    dynamic_feature_loading_error.hide()
                    open_feature.show()

                    currentSessionId = NO_SESSION
                }
                SplitInstallSessionStatus.CANCELING -> {
                    Log.i(classTag, "errorCode: ${it.errorCode()}")
                    Log.i(classTag, "status: CANCELING")
                }
                SplitInstallSessionStatus.CANCELED -> {
                    Log.i(classTag, "errorCode: ${it.errorCode()}")
                    Log.i(classTag, "status: CANCELED")
                    currentSessionId = NO_SESSION
                }
                else -> {
                    Log.i(classTag, "errorCode: ${it.errorCode()}")
                    Log.i(classTag, "status: UNEXPECTED? NOT LISTED?")
                }
            }
        }

        if (currentSessionId == NO_SESSION) {
            Log.i(classTag, "currentSessionId is NO_SESSION: start install")
            splitInstallManager.registerListener(changesListener)
            splitInstallManager.startInstall(SplitInstallRequest.newBuilder().addModule(Feature.TheMeg(requireContext()).name).build())
                .addOnFailureListener {
                    Log.i(classTag, "failure task")
                }
        }
        Log.i(classTag, "currentSessionId is set")
        Log.i(classTag, "fetchAllFeatures() --> end")
    }

    private fun installFeatures(features: List<Feature>) = features.forEach { feature ->
//        dynamicFeatureDependencyProvider.installDependencies(feature.dependencies.map { moduleClass ->
//            moduleClass.newInstance() as Module
//        })
    }


    /*private var listener: OnFragmentInteractionListener? = null

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }*/
}
