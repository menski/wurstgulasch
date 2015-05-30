$TTL 300S
@       IN      SOA     camunda.example.com. mail.camunda.example.com. (
                        2015053001      ; Serial
                                5M      ; Refresh
                               10M      ; Retry
                                1W      ; Expire
                                0S )    ; NX (TTL Negativ Cache)

@                               IN      NS      ns.camunda.example.com.

ns                              IN      A       127.0.0.1

