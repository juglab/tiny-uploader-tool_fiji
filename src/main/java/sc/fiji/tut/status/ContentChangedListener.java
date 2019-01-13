package sc.fiji.tut.status;

import java.util.EventListener;

public interface ContentChangedListener extends EventListener {
    void contentChanged(ContentChangedEvent e);
}
