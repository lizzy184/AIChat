def create_sse_event(
    event:str,
    data:str
):

    return (
        f"event:{event}\n"
        f"data:{data}\n\n"
    )