package Customer;

public class CustomerFile {
    byte[] byteFile;

    public CustomerFile() {}

    public CustomerFile(byte[] byteFile) {
        this.setByteFile(byteFile);
    }

    public byte[] getByteFile() {
        return byteFile;
    }
    
    public void setByteFile(byte[] byteFile) {
        this.byteFile = byteFile;
    }
}
