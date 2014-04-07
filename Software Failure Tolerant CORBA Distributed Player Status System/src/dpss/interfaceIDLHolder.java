package dpss;

/**
 * Holder class for : interfaceIDL
 * 
 * @author OpenORB Compiler
 */
final public class interfaceIDLHolder
        implements org.omg.CORBA.portable.Streamable
{
    /**
     * Internal interfaceIDL value
     */
    public dpss.interfaceIDL value;

    /**
     * Default constructor
     */
    public interfaceIDLHolder()
    { }

    /**
     * Constructor with value initialisation
     * @param initial the initial value
     */
    public interfaceIDLHolder(dpss.interfaceIDL initial)
    {
        value = initial;
    }

    /**
     * Read interfaceIDL from a marshalled stream
     * @param istream the input stream
     */
    public void _read(org.omg.CORBA.portable.InputStream istream)
    {
        value = interfaceIDLHelper.read(istream);
    }

    /**
     * Write interfaceIDL into a marshalled stream
     * @param ostream the output stream
     */
    public void _write(org.omg.CORBA.portable.OutputStream ostream)
    {
        interfaceIDLHelper.write(ostream,value);
    }

    /**
     * Return the interfaceIDL TypeCode
     * @return a TypeCode
     */
    public org.omg.CORBA.TypeCode _type()
    {
        return interfaceIDLHelper.type();
    }

}
