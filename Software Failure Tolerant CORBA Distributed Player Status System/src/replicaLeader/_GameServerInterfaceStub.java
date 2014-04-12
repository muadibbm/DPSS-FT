package replicaLeader;

/**
 * Interface definition: GameServerInterface.
 * 
 * @author OpenORB Compiler
 */
public class _GameServerInterfaceStub extends org.omg.CORBA.portable.ObjectImpl
        implements GameServerInterface
{
    static final String[] _ids_list =
    {
        "IDL:game/GameServerInterface:1.0"
    };

    public String[] _ids()
    {
     return _ids_list;
    }

    private final static Class _opsClass = replicaLeader.GameServerInterfaceOperations.class;

    /**
     * Operation createPlayerAccount
     */
    public String createPlayerAccount(String FirstName, String LastName, String Age, String Username, String Password, String IPAddress)
    {
        while(true)
        {
            if (!this._is_local())
            {
                org.omg.CORBA.portable.InputStream _input = null;
                try
                {
                    org.omg.CORBA.portable.OutputStream _output = this._request("createPlayerAccount",true);
                    _output.write_string(FirstName);
                    _output.write_string(LastName);
                    _output.write_string(Age);
                    _output.write_string(Username);
                    _output.write_string(Password);
                    _output.write_string(IPAddress);
                    _input = this._invoke(_output);
                    String _arg_ret = _input.read_string();
                    return _arg_ret;
                }
                catch(org.omg.CORBA.portable.RemarshalException _exception)
                {
                    continue;
                }
                catch(org.omg.CORBA.portable.ApplicationException _exception)
                {
                    String _exception_id = _exception.getId();
                    throw new org.omg.CORBA.UNKNOWN("Unexpected User Exception: "+ _exception_id);
                }
                finally
                {
                    this._releaseReply(_input);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _so = _servant_preinvoke("createPlayerAccount",_opsClass);
                if (_so == null)
                   continue;
                replicaLeader.GameServerInterfaceOperations _self = (replicaLeader.GameServerInterfaceOperations) _so.servant;
                try
                {
                    return _self.createPlayerAccount( FirstName,  LastName,  Age,  Username,  Password,  IPAddress);
                }
                finally
                {
                    _servant_postinvoke(_so);
                }
            }
        }
    }

    /**
     * Operation playerSignIn
     */
    public String playerSignIn(String UserName, String Password, String IPAddres)
    {
        while(true)
        {
            if (!this._is_local())
            {
                org.omg.CORBA.portable.InputStream _input = null;
                try
                {
                    org.omg.CORBA.portable.OutputStream _output = this._request("playerSignIn",true);
                    _output.write_string(UserName);
                    _output.write_string(Password);
                    _output.write_string(IPAddres);
                    _input = this._invoke(_output);
                    String _arg_ret = _input.read_string();
                    return _arg_ret;
                }
                catch(org.omg.CORBA.portable.RemarshalException _exception)
                {
                    continue;
                }
                catch(org.omg.CORBA.portable.ApplicationException _exception)
                {
                    String _exception_id = _exception.getId();
                    throw new org.omg.CORBA.UNKNOWN("Unexpected User Exception: "+ _exception_id);
                }
                finally
                {
                    this._releaseReply(_input);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _so = _servant_preinvoke("playerSignIn",_opsClass);
                if (_so == null)
                   continue;
                replicaLeader.GameServerInterfaceOperations _self = (replicaLeader.GameServerInterfaceOperations) _so.servant;
                try
                {
                    return _self.playerSignIn( UserName,  Password,  IPAddres);
                }
                finally
                {
                    _servant_postinvoke(_so);
                }
            }
        }
    }

    /**
     * Operation playerSignOut
     */
    public String playerSignOut(String Username, String IPAddress)
    {
        while(true)
        {
            if (!this._is_local())
            {
                org.omg.CORBA.portable.InputStream _input = null;
                try
                {
                    org.omg.CORBA.portable.OutputStream _output = this._request("playerSignOut",true);
                    _output.write_string(Username);
                    _output.write_string(IPAddress);
                    _input = this._invoke(_output);
                    String _arg_ret = _input.read_string();
                    return _arg_ret;
                }
                catch(org.omg.CORBA.portable.RemarshalException _exception)
                {
                    continue;
                }
                catch(org.omg.CORBA.portable.ApplicationException _exception)
                {
                    String _exception_id = _exception.getId();
                    throw new org.omg.CORBA.UNKNOWN("Unexpected User Exception: "+ _exception_id);
                }
                finally
                {
                    this._releaseReply(_input);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _so = _servant_preinvoke("playerSignOut",_opsClass);
                if (_so == null)
                   continue;
                replicaLeader.GameServerInterfaceOperations _self = (replicaLeader.GameServerInterfaceOperations) _so.servant;
                try
                {
                    return _self.playerSignOut( Username,  IPAddress);
                }
                finally
                {
                    _servant_postinvoke(_so);
                }
            }
        }
    }

    /**
     * Operation transferAccount
     */
    public String transferAccount(String Username, String Password, String oldIPAddress, String newIPAddress)
    {
        while(true)
        {
            if (!this._is_local())
            {
                org.omg.CORBA.portable.InputStream _input = null;
                try
                {
                    org.omg.CORBA.portable.OutputStream _output = this._request("transferAccount",true);
                    _output.write_string(Username);
                    _output.write_string(Password);
                    _output.write_string(oldIPAddress);
                    _output.write_string(newIPAddress);
                    _input = this._invoke(_output);
                    String _arg_ret = _input.read_string();
                    return _arg_ret;
                }
                catch(org.omg.CORBA.portable.RemarshalException _exception)
                {
                    continue;
                }
                catch(org.omg.CORBA.portable.ApplicationException _exception)
                {
                    String _exception_id = _exception.getId();
                    throw new org.omg.CORBA.UNKNOWN("Unexpected User Exception: "+ _exception_id);
                }
                finally
                {
                    this._releaseReply(_input);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _so = _servant_preinvoke("transferAccount",_opsClass);
                if (_so == null)
                   continue;
                replicaLeader.GameServerInterfaceOperations _self = (replicaLeader.GameServerInterfaceOperations) _so.servant;
                try
                {
                    return _self.transferAccount( Username,  Password,  oldIPAddress,  newIPAddress);
                }
                finally
                {
                    _servant_postinvoke(_so);
                }
            }
        }
    }

    /**
     * Operation getPlayerStatus
     */
    public String getPlayerStatus(String AdminUserName, String AdminPassword, String AdminIPAddress)
    {
        while(true)
        {
            if (!this._is_local())
            {
                org.omg.CORBA.portable.InputStream _input = null;
                try
                {
                    org.omg.CORBA.portable.OutputStream _output = this._request("getPlayerStatus",true);
                    _output.write_string(AdminUserName);
                    _output.write_string(AdminPassword);
                    _output.write_string(AdminIPAddress);
                    _input = this._invoke(_output);
                    String _arg_ret = _input.read_string();
                    return _arg_ret;
                }
                catch(org.omg.CORBA.portable.RemarshalException _exception)
                {
                    continue;
                }
                catch(org.omg.CORBA.portable.ApplicationException _exception)
                {
                    String _exception_id = _exception.getId();
                    throw new org.omg.CORBA.UNKNOWN("Unexpected User Exception: "+ _exception_id);
                }
                finally
                {
                    this._releaseReply(_input);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _so = _servant_preinvoke("getPlayerStatus",_opsClass);
                if (_so == null)
                   continue;
                replicaLeader.GameServerInterfaceOperations _self = (replicaLeader.GameServerInterfaceOperations) _so.servant;
                try
                {
                    return _self.getPlayerStatus( AdminUserName,  AdminPassword,  AdminIPAddress);
                }
                finally
                {
                    _servant_postinvoke(_so);
                }
            }
        }
    }

    /**
     * Operation suspendAccount
     */
    public String suspendAccount(String AdminUserName, String AdminPassword, String AdminIPAddress, String UsernametoSuspend)
    {
        while(true)
        {
            if (!this._is_local())
            {
                org.omg.CORBA.portable.InputStream _input = null;
                try
                {
                    org.omg.CORBA.portable.OutputStream _output = this._request("suspendAccount",true);
                    _output.write_string(AdminUserName);
                    _output.write_string(AdminPassword);
                    _output.write_string(AdminIPAddress);
                    _output.write_string(UsernametoSuspend);
                    _input = this._invoke(_output);
                    String _arg_ret = _input.read_string();
                    return _arg_ret;
                }
                catch(org.omg.CORBA.portable.RemarshalException _exception)
                {
                    continue;
                }
                catch(org.omg.CORBA.portable.ApplicationException _exception)
                {
                    String _exception_id = _exception.getId();
                    throw new org.omg.CORBA.UNKNOWN("Unexpected User Exception: "+ _exception_id);
                }
                finally
                {
                    this._releaseReply(_input);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _so = _servant_preinvoke("suspendAccount",_opsClass);
                if (_so == null)
                   continue;
                replicaLeader.GameServerInterfaceOperations _self = (replicaLeader.GameServerInterfaceOperations) _so.servant;
                try
                {
                    return _self.suspendAccount( AdminUserName,  AdminPassword,  AdminIPAddress,  UsernametoSuspend);
                }
                finally
                {
                    _servant_postinvoke(_so);
                }
            }
        }
    }

}
