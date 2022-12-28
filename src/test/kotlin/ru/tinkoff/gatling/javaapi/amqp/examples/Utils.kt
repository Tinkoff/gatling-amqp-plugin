package ru.tinkoff.gatling.javaapi.aqmp.examples

object Utils {
    private val counter = AtomicInteger(1)
    var idFeeder = Stream.generate(
        Supplier {
            Collections.singletonMap<String, Any>(
                "id",
                counter.getAndIncrement()
            )
        } as Supplier<Map<String, Any>>).iterator()
}