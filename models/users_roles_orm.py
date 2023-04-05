<<<<<<< HEAD
from sqlalchemy import Column, String, Integer, Date, Table, ForeignKey

from base.sql_base import Base

user_roles_relationship = Table(
    'Join_UR', Base.metadata,
    Column('ID_U', Integer, ForeignKey('Users.ID')),
    Column('ID_R', Integer, ForeignKey('Roles.ID')),
    extend_existing=True
)
=======
from sqlalchemy import Column, String, Integer, Date, Table, ForeignKey

from base.sql_base import Base

user_roles_relationship = Table(
    'Join_UR', Base.metadata,
    Column('ID_U', Integer, ForeignKey('Users.ID')),
    Column('ID_R', Integer, ForeignKey('Roles.ID')),
    extend_existing=True
)
>>>>>>> e7eabf4 (IDM module)
