import logging

from spyne import Application, rpc, ServiceBase, Integer, Double
from spyne.protocol.soap import Soap11
from spyne.server.wsgi import WsgiApplication
from repositories import user_repository
from repositories.user_repository import *

class SqlService(ServiceBase):
    @rpc(_returns=str)
    def getRoles(ctx):
        return role_repository.get_roles()

    @rpc(str, _returns=str)
    def createRole(ctx, rolename):
        return role_repository.create_role(rolename)

    @rpc(str, str, str, _returns=str)
    def createUser(ctx, username, password, token):
        return user_repository.create_user(username, password, token)

    @rpc(str, _returns=str)
    def getUsers(ctx, token):
        return user_repository.get_users(token)

    @rpc(str, str, str, _returns=str)
    def changeUserPassword(ctx, username, password, token):
        return user_repository.change_user_password(username, password, token)

    @rpc(str, str, _returns=str)
    def deleteUser(ctx, username, token):
        return user_repository.delete_user(username, token)

    @rpc(str, str, str, _returns=str)
    def addRoleToUser(ctx, username, rolename, token):
        return user_repository.add_role_to_user(username, rolename, token)

    @rpc(str, str, str, str, _returns=str)
    def changeUserRole(ctx, username, old_rolename, new_rolename, token):
        return user_repository.change_user_role(username, old_rolename, new_rolename, token)

    @rpc(str, str, _returns=str)
    def autentificare(ctx, username, password):
        return user_repository.login(username, password)

    @rpc(str, _returns=str)
    def autorizare(ctx, token):
        return user_repository.autorizare(token)

    @rpc(str, _returns=str)
    def logout(ctx, token):
        return user_repository.logout(token)