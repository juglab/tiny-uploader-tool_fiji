package sc.fiji.tut.status;

import java.util.EventObject;

public class ContentChangedEvent extends EventObject {
    public ContentChangedEvent(Object source) {
        super(source);
    }
}
