from src.common.utils.config_handler import load_config
from src.common.utils.path_util import get_abstract_path

backend_config = load_config(config_path=get_abstract_path('src/config/backend.yml'))
model_config = load_config(config_path=get_abstract_path('src/config/model.yml'))
prompt_config = load_config(config_path=get_abstract_path('src/config/prompt.yml'))
rabbitmq_config = load_config(config_path=get_abstract_path('src/config/rabbitmq.yml'))
redis_config = load_config(config_path=get_abstract_path('src/config/redis.yml'))
rag_config = load_config(config_path=get_abstract_path('src/config/rag.yml'))
