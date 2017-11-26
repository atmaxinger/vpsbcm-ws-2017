package at.ac.tuwien.complang.vpsbcm.robnur.shared.plants;

import java.io.Serializable;
import java.util.UUID;

public class Idable implements Serializable{
    private final String id;

    public String getId() {
        return id;
    }

    public Idable() {
        UUID uuid = UUID.randomUUID();
        this.id = uuid.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == this) return true;
        if(obj instanceof Idable) {
            if(((Idable) obj).getId().equals(this.getId())) {
                return true;
            }
        }

        return false;
    }
}
