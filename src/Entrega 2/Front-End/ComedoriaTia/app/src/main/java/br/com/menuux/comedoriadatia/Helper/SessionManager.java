
package br.com.menuux.comedoriadatia.Helper;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String PREF_NAME = "UserSession";
    private static final String KEY_USER_ROLE = "user_role";
    private static final String DEFAULT_ROLE = "cliente";

    private final SharedPreferences pref;
    private final SharedPreferences.Editor editor;
    private final Context _context;

    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void setUserRole(String role) {
        editor.putString(KEY_USER_ROLE, role);
        editor.commit();
    }

    public String getUserRole() {
        return pref.getString(KEY_USER_ROLE, DEFAULT_ROLE);
    }

    public boolean isAdmin() {
        return "admin".equals(getUserRole());
    }

    public void clearSession() {
        editor.clear();
        editor.commit();
    }
}
