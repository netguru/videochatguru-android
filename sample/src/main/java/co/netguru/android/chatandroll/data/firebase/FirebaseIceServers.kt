package co.netguru.android.chatandroll.data.firebase

import co.netguru.android.chatandroll.common.extension.rxSingleValue
import co.netguru.android.chatandroll.data.model.IceServerFirebase
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import io.reactivex.Maybe
import org.webrtc.PeerConnection
import javax.inject.Inject

class FirebaseIceServers @Inject constructor(firebaseDatabase: FirebaseDatabase) {

    companion object {
        private const val ICE_SERVERS_PATH = "ice_servers"
    }

    private val firebaseIceServersReference by lazy { firebaseDatabase.getReference(ICE_SERVERS_PATH) }

    fun getIceServers(): Maybe<List<PeerConnection.IceServer>> = firebaseIceServersReference
            .rxSingleValue(object : GenericTypeIndicator<List<@JvmSuppressWildcards IceServerFirebase>>() {})
            .map { it.map { it.toIceServer() } }

}