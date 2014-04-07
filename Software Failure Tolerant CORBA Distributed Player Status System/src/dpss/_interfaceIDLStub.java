package dpss;

/**
 * Interface definition: interfaceIDL.
 * 
 * @author OpenORB Compiler
 */
public class _interfaceIDLStub extends org.omg.CORBA.portable.ObjectImpl
        implements interfaceIDL
{
    static final String[] _ids_list =
    {
        "IDL:dpss/interfaceIDL:1.0"
    };

    public String[] _ids()
    {
     return _ids_list;
    }

    private final static Class _opsClass = dpss.interfaceIDLOperations.class;

    /**
     * Operation createPlayerAccount
     */
    public boolean createPlayerAccount(String pFirstName, String pLastName, int pAge, String pUsername, String pPassword, String pIPAddress)
    {
        while(true)
        {
            if (!this._is_local())
            {
                org.omg.CORBA.portable.InputStream _input = null;
                try
                {
                    org.omg.CORBA.portable.OutputStream _output = this._request("createPlayerAccount",true);
                    _output.write_string(pFirstName);
                    _output.write_string(pLastName);
                    _output.write_long(pAge);
                    _output.write_string(pUsername);
                    _output.write_string(pPassword);
                    _output.write_string(pIPAddress);
                    _input = this._invoke(_output);
                    boolean _arg_ret = _input.read_boolean();
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
                dpss.interfaceIDLOperations _self = (dpss.interfaceIDLOperations) _so.servant;
                try
                {
                    return _self.createPlayerAccount( pFirstName,  pLastName,  pAge,  pUsername,  pPassword,  pIPAddress);
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
    public boolean playerSignIn(String pUsername, String pPassword, String pIPAddress)
    {
        while(true)
        {
            if (!this._is_local())
            {
                org.omg.CORBA.portable.InputStream _input = null;
                try
                {
                    org.omg.CORBA.portable.OutputStream _output = this._request("playerSignIn",true);
                    _output.write_string(pUsername);
                    _output.write_string(pPassword);
                    _output.write_string(pIPAddress);
                    _input = this._invoke(_output);
                    boolean _arg_ret = _input.read_boolean();
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
                dpss.interfaceIDLOperations _self = (dpss.interfaceIDLOperations) _so.servant;
                try
                {
                    return _self.playerSignIn( pUsername,  pPassword,  pIPAddress);
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
    public boolean playerSignOut(String pUsername, String pIPAddress)
    {
        while(true)
        {
            if (!this._is_local())
            {
                org.omg.CORBA.portable.InputStream _input = null;
                try
                {
                    org.omg.CORBA.portable.OutputStream _output = this._request("playerSignOut",true);
                    _output.write_string(pUsername);
                    _output.write_string(pIPAddress);
                    _input = this._invoke(_output);
                    boolean _arg_ret = _input.read_boolean();
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
                dpss.interfaceIDLOperations _self = (dpss.interfaceIDLOperations) _so.servant;
                try
                {
                    return _self.playerSignOut( pUsername,  pIPAddress);
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
    public boolean transferAccount(String pUsername, String pPassword, String pOldIPAddress, String pNewIPAddress)
    {
        while(true)
        {
            if (!this._is_local())
            {
                org.omg.CORBA.portable.InputStream _input = null;
                try
                {
                    org.omg.CORBA.portable.OutputStream _output = this._request("transferAccount",true);
                    _output.write_string(pUsername);
                    _output.write_string(pPassword);
                    _output.write_string(pOldIPAddress);
                    _output.write_string(pNewIPAddress);
                    _input = this._invoke(_output);
                    boolean _arg_ret = _input.read_boolean();
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
                dpss.interfaceIDLOperations _self = (dpss.interfaceIDLOperations) _so.servant;
                try
                {
                    return _self.transferAccount( pUsername,  pPassword,  pOldIPAddress,  pNewIPAddress);
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
    public boolean getPlayerStatus(String pAdminUsername, String pAdminPassword, String pIPAddress)
    {
        while(true)
        {
            if (!this._is_local())
            {
                org.omg.CORBA.portable.InputStream _input = null;
                try
                {
                    org.omg.CORBA.portable.OutputStream _output = this._request("getPlayerStatus",true);
                    _output.write_string(pAdminUsername);
                    _output.write_string(pAdminPassword);
                    _output.write_string(pIPAddress);
                    _input = this._invoke(_output);
                    boolean _arg_ret = _input.read_boolean();
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
                dpss.interfaceIDLOperations _self = (dpss.interfaceIDLOperations) _so.servant;
                try
                {
                    return _self.getPlayerStatus( pAdminUsername,  pAdminPassword,  pIPAddress);
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
    public boolean suspendAccount(String pAdminUsername, String pAdminPassword, String pIPAddress, String pUsernameToSuspend)
    {
        while(true)
        {
            if (!this._is_local())
            {
                org.omg.CORBA.portable.InputStream _input = null;
                try
                {
                    org.omg.CORBA.portable.OutputStream _output = this._request("suspendAccount",true);
                    _output.write_string(pAdminUsername);
                    _output.write_string(pAdminPassword);
                    _output.write_string(pIPAddress);
                    _output.write_string(pUsernameToSuspend);
                    _input = this._invoke(_output);
                    boolean _arg_ret = _input.read_boolean();
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
                dpss.interfaceIDLOperations _self = (dpss.interfaceIDLOperations) _so.servant;
                try
                {
                    return _self.suspendAccount( pAdminUsername,  pAdminPassword,  pIPAddress,  pUsernameToSuspend);
                }
                finally
                {
                    _servant_postinvoke(_so);
                }
            }
        }
    }

}
