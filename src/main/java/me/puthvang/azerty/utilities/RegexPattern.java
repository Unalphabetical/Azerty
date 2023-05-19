package me.puthvang.azerty.utilities;

import java.util.regex.Pattern;

public class RegexPattern {

    static public Pattern DISCORD_TOKEN = Pattern.compile("[MN][A-Za-z\\d]{23}\\.[\\w-]{6}\\.[\\w-]{27}");

}
