package Session;

import java.util.HashMap;

public class CustomerSession {
    HashMap<String, Object> session = new HashMap<>();

    public CustomerSession() {}

    public CustomerSession(HashMap<String, Object> session) {
        this.session = session;
    }

    public HashMap<String, Object> getSession() {
        return session;
    }

    public void setSession(HashMap<String, Object> session) {
        this.session = session;
    }
    
    public void add(String key, Object value) {
        this.getSession().put(key, value);
    }
    
    public Object get(String key) {
        Object value = this.getSession().get(key);
        return value;
    }

    public void update(String key, Object value) {
        this.getSession().replace(key, value);
    }

    public void delete(String key) {
        this.getSession().remove(key);
    }
}