from cache.client import redis_client


class RedisService:

    def __init__(self, redis_client):
        self.redis = redis_client

    def add_blacklist(
        self,
        key: str,
        expire_seconds: int
    ):
        self.redis.set(
            key,
            "blacklist",
            ex=expire_seconds
        )

    def is_blacklisted(
        self,
        key: str
    ) -> bool:

        return self.redis.get(key) is not None


redis_service = RedisService(redis_client)