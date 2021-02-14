package tz.okronos.core.property;

/**
 * A instance of OneWayConverter allows to convert from a type S to a type D.
 *
 * @param <S> the type of the source.
 * @param <D> the type of the destination.
 */
@FunctionalInterface
public interface OneWayConverter<S, D> {
    public D convert(S source);
}