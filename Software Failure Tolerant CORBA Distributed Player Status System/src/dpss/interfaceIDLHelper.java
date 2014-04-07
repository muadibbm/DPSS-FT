package dpss;

/** 
 * Helper class for : interfaceIDL
 *  
 * @author OpenORB Compiler
 */ 
public class interfaceIDLHelper
{
    /**
     * Insert interfaceIDL into an any
     * @param a an any
     * @param t interfaceIDL value
     */
    public static void insert(org.omg.CORBA.Any a, dpss.interfaceIDL t)
    {
        a.insert_Object(t , type());
    }

    /**
     * Extract interfaceIDL from an any
     *
     * @param a an any
     * @return the extracted interfaceIDL value
     */
    public static dpss.interfaceIDL extract( org.omg.CORBA.Any a )
    {
        if ( !a.type().equivalent( type() ) )
        {
            throw new org.omg.CORBA.MARSHAL();
        }
        try
        {
            return dpss.interfaceIDLHelper.narrow( a.extract_Object() );
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
     * Return the interfaceIDL TypeCode
     * @return a TypeCode
     */
    public static org.omg.CORBA.TypeCode type()
    {
        if (_tc == null) {
            org.omg.CORBA.ORB orb = org.omg.CORBA.ORB.init();
            _tc = orb.create_interface_tc( id(), "interfaceIDL" );
        }
        return _tc;
    }

    /**
     * Return the interfaceIDL IDL ID
     * @return an ID
     */
    public static String id()
    {
        return _id;
    }

    private final static String _id = "IDL:dpss/interfaceIDL:1.0";

    /**
     * Read interfaceIDL from a marshalled stream
     * @param istream the input stream
     * @return the readed interfaceIDL value
     */
    public static dpss.interfaceIDL read(org.omg.CORBA.portable.InputStream istream)
    {
        return(dpss.interfaceIDL)istream.read_Object(dpss._interfaceIDLStub.class);
    }

    /**
     * Write interfaceIDL into a marshalled stream
     * @param ostream the output stream
     * @param value interfaceIDL value
     */
    public static void write(org.omg.CORBA.portable.OutputStream ostream, dpss.interfaceIDL value)
    {
        ostream.write_Object((org.omg.CORBA.portable.ObjectImpl)value);
    }

    /**
     * Narrow CORBA::Object to interfaceIDL
     * @param obj the CORBA Object
     * @return interfaceIDL Object
     */
    public static interfaceIDL narrow(org.omg.CORBA.Object obj)
    {
        if (obj == null)
            return null;
        if (obj instanceof interfaceIDL)
            return (interfaceIDL)obj;

        if (obj._is_a(id()))
        {
            _interfaceIDLStub stub = new _interfaceIDLStub();
            stub._set_delegate(((org.omg.CORBA.portable.ObjectImpl)obj)._get_delegate());
            return stub;
        }

        throw new org.omg.CORBA.BAD_PARAM();
    }

    /**
     * Unchecked Narrow CORBA::Object to interfaceIDL
     * @param obj the CORBA Object
     * @return interfaceIDL Object
     */
    public static interfaceIDL unchecked_narrow(org.omg.CORBA.Object obj)
    {
        if (obj == null)
            return null;
        if (obj instanceof interfaceIDL)
            return (interfaceIDL)obj;

        _interfaceIDLStub stub = new _interfaceIDLStub();
        stub._set_delegate(((org.omg.CORBA.portable.ObjectImpl)obj)._get_delegate());
        return stub;

    }

}
