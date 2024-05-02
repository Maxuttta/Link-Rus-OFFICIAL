package link.download.ru

import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError

class AppChildEventListener(val onSuccess: (DataSnapshot, Int) -> Unit) : ChildEventListener {
    override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
        onSuccess(snapshot, 1)
    }

    override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
        onSuccess(snapshot, 2)
    }

    override fun onChildRemoved(snapshot: DataSnapshot) {
        onSuccess(snapshot, 3)
    }

    override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
        // Not implemented
    }

    override fun onCancelled(error: DatabaseError) {
        // Not implemented
    }
}