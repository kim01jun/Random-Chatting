package comkimwonjun.github.jumpup;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * JumpUp
 * Created by KimWonJun on 7/24/2018.
 */
public class References {
    private static final DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
    private static final StorageReference storageRef = FirebaseStorage.getInstance().getReference();

    public static DatabaseReference getDbRef() {
        return databaseRef;
    }

    public static StorageReference getStorageRef() {
        return storageRef;
    }
}
