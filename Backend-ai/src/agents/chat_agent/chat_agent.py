from langchain.agents import create_agent

from src.common.factory import chat_model
from src.utils.get_student_reference import get_student_profile, list_latest_snapshots
from src.utils.prompt_loader import load_prompt
from src.utils.redis_util import get_user_context

chat_agent = create_agent(
    model=chat_model,
    tools=[get_user_context, get_student_profile, list_latest_snapshots],
    system_prompt=load_prompt("base_chat_prompt.txt"),
)