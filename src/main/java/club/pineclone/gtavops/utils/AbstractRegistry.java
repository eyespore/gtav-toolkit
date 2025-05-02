package club.pineclone.gtavops.utils;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class AbstractRegistry<T> {

    protected final List<T> registry = new ArrayList<T>();

}
