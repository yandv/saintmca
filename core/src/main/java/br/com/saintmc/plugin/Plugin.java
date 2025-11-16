package br.com.saintmc.plugin;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Plugin {

    private String name;
    private String version;

    public boolean isNewerVersionThan(String otherVersion) {
        if (this.version == null || otherVersion == null) {
            return false;
        }

        String[] current = this.version.split("\\.");
        String[] other = otherVersion.split("\\.");
        int len = Math.max(current.length, other.length);

        for (int i = 0; i < len; i++) {
            int cv = i < current.length ? Integer.parseInt(current[i]) : 0;
            int ov = i < other.length ? Integer.parseInt(other[i]) : 0;
            if (cv > ov) return true;
            if (cv < ov) return false;
        }
        return false;
    }
}

