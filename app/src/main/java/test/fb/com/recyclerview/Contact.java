package test.fb.com.recyclerview;

import java.util.ArrayList;
import java.util.List;

public class Contact {
    String mName;

    public Contact(String mName) {
        this.mName = mName;
    }

    static List<Contact> getContacts(int size) {
        List<Contact> contacts = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            contacts.add(new Contact("Person " + i));
        }
        return contacts;
    }
}
