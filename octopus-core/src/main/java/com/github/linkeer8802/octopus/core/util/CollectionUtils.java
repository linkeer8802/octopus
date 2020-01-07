package com.github.linkeer8802.octopus.core.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 集合操作工具类
 * @author weird
 */
public final class CollectionUtils {

    public static <T, R> List<R> transform(List<T> list, Function<? super T, ? extends R> mapper) {
        if (list == null) {
            return new ArrayList<>(0);
        }
        return list.stream().map(mapper).collect(Collectors.toList());
    }

    public static <T, R> Set<R> transform(Set<T> set, Function<? super T, ? extends R> mapper) {
        if (set == null) {
            return Collections.emptySet();
        }
        return set.stream().map(mapper).collect(Collectors.toSet());
    }
}
