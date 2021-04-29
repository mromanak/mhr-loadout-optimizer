package com.mromanak.loadoutoptimizer.utils;

import com.google.common.base.Joiner;
import com.mromanak.loadoutoptimizer.model.jpa.ArmorType;
import com.mromanak.loadoutoptimizer.model.jpa.SetType;
import org.apache.commons.lang3.StringUtils;

import java.text.Normalizer;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

public class NameUtils {

    private static final Pattern NONLATIN = Pattern.compile("[^\\w_-]");
    private static final Pattern SEPARATORS = Pattern.compile("[\\s\\p{Punct}&&[^-]]");
    private static final Joiner WHITESPACE_JOINER = Joiner.on(' ').skipNulls();

    public static String toSlug(String name) {
        Objects.requireNonNull(name, "Name must be non-null");

        String symbolsExpanded = StringUtils.
            replaceEach(name, new String[]{"α", "β", "γ", "+"}, new String[]{"alpha", "beta", "gamma", "plus"});
        String noSeparators = SEPARATORS.matcher(symbolsExpanded).replaceAll("-");
        String normalized = Normalizer.normalize(noSeparators, Normalizer.Form.NFD);
        String slug = NONLATIN.matcher(normalized).replaceAll("");
        return slug.toLowerCase(Locale.ENGLISH).replaceAll("-{2,}", "-").replaceAll("^-|-$", "");
    }

    public static String toSlug(String setName, ArmorType armorType, SetType setType) {
        String armorTypeName = armorType == null ? null : armorType.getName();
        String setTypeName = setType == null ? null : setType.getName();
        return toSlug(WHITESPACE_JOINER.join(setName, armorTypeName, setTypeName));
    }
}
