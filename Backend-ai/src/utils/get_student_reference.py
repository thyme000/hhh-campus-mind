import os

from dotenv import load_dotenv
from sqlalchemy import select, desc, create_engine
from sqlalchemy.orm import Session
from langchain.tools import tool

from src.data.profile_snapshots import ProfileSnapshot, snapshot_to_dict
from src.data.student_profile import StudentProfile, student_profile_to_dict

load_dotenv()

# 数据库连接
engine = create_engine(
    f"postgresql+psycopg://{os.getenv('PG_USER')}:{os.getenv('PG_PASSWORD')}@{os.getenv('PG_HOST')}:{os.getenv('PG_PORT')}/{os.getenv("PG_DB")}"
)


def list_latest_snapshots(user_id: int, limit: int = 5):
    """
    获取用户最新的profile snapshot(默认5条)
    """
    with Session(engine) as session:
        stmt = (
            select(ProfileSnapshot)
            .where(ProfileSnapshot.user_id == user_id)
            .order_by(desc(ProfileSnapshot.created_at))
            .limit(limit)
        )
        return [snapshot_to_dict(raw) for raw in session.scalars(stmt).all()]


def get_student_profile(user_id: int):
    """
    获取学生画像
    :param user_id:
    :return:
    """
    with Session(engine) as session:
        stmt = (
            select(StudentProfile)
            .where(StudentProfile.user_id == user_id)
            .where(StudentProfile.is_delete == 0)
        )
        return student_profile_to_dict(session.scalars(stmt).first())

if __name__ == '__main__':
    rows = list_latest_snapshots(1, 5)
    profile = get_student_profile(1)
    print(profile)
    print(rows)
