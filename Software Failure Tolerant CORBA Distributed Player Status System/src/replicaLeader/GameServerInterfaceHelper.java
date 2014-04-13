package replicaLeader;

/** 
 * Helper class for : GameServerInterface
 *  
 * @author OpenORB Compiler
 */ 
public class GameServerInterfaceHelper
{
    /**
     * Insert GameServerInterface into an any
     * @param a an any
     * @param t GameServerInterface value
     */
    public static void insert(org.omg.CORBA.Any a, replicaLeader.GameServerInterface t)
    {
        a.insert_Object(t , type());
    }

    /**
     * Extract GameServerInterface from an any
     *
     * @param a an any
     * @return the extracted GameServerInterface value
     */
    public static replicaLeader.GameServerInterface extract( org.omg.CORBA.Any a )
    {
        if ( !a.type().equivalent( type() ) )
        {
            throw new org.omg.CORBA.MARSHAL();
        }
        try
        {
            return replicaLeader.GameServerInterfaceHelper.narrow( a.extract_Object() );
        }
        catch ( final org.omg.CORBA.BAD_PARAM e )
        {
            throw new org.omg.CORBA.MARSHAL(e.getMessage());
        }
    }

    //
    // Internal TypeCode value
    //
    private static org.omg.CORBA.TypeCode _tc = null;

    /**
     * Return the GameServerInterface TypeCode
     * @return a TypeCode
     */
    public static org.omg.CORBA.TypeCode type()
    {
        if (_tc == null) {
            org.omg.CORBA.ORB orb = org.omg.CORBA.ORB.init();
            _tc = orb.create_interface_tc( id(), "GameServerInterface" );
        }
        return _tc;
    }

    /**
     * Return the GameServerInterface IDL ID
     * @return an ID
     */
    public static String id()
    {
        return _id;
    }

    private final static String _id = "IDL:game/GameServerInterface:1.0";

    /**
     * Read GameServerInterface from a marshalled stream
     * @param istream the input stream
     * @return the readed GameServerInterface value
     */
    public static replicaLeader.GameServerInterface read(org.omg.CORBA.portable.InputStream istream)
    {
        return(replicaLeader.GameServerInterface)istream.read_Object(replicaLeader._GameServerInterfaceStub.class);
    }

    /**
     * Write GameServerInterface into a marshalled stream
     * @param ostream the output stream
     * @param value GameServerInterface value
     */
    public static void write(org.omg.CORBA.portable.OutputStream ostream, replicaLeader.GameServerInterface value)
    {
        ostream.write_Object((org.omg.CORBA.portable.ObjectImpl)value);
    }

    /**
     * Narrow CORBA::Object to GameServerInterface
     * @param obj the CORBA Object
     * @return GameServerInterface Object
     */
    public static GameServerInterface narrow(org.omg.CORBA.Object obj)
    {
        if (obj == null)
            return null;
        if (obj instanceof GameServerInterface)
            return (GameServerInterface)obj;

        if (obj._is_a(id()))
        {
            _GameServerInterfaceStub stub = new _GameServerInterfaceStub();
            stub._set_delegate(((org.omg.CORBA.portable.ObjectImpl)obj)._get_delegate());
            return stub;
        }

        throw new org.omg.CORBA.BAD_PARAM();
    }

    /**
     * Unchecked Narrow CORBA::Object to GameServerInterface
     * @param obj the CORBA Object
     * @return GameServerInterface Object
     */
    public static GameServerInterface unchecked_narrow(org.omg.CORBA.Object obj)
    {
        if (obj == null)
            return null;
        if (obj instanceof GameServerInterface)
            return (GameServerInterface)obj;

        _GameServerInterfaceStub stub = new _GameServerInterfaceStub();
        stub._set_delegate(((org.omg.CORBA.portable.ObjectImpl)obj)._get_delegate());
        return stub;

    }

}
