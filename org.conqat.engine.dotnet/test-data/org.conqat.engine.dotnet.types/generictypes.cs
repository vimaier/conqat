using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace GenericsTestLibrary
{
    public struct Test <T>
    {

    }

    delegate void SampleDelegate(string message);
    
    public class Class1
    {

        delegate void SampleDelegate2(string message);
    
    }


    public class Class1<T, X>
    {

    }
    
    public class Class1<T, out X>
    {

    }

    public class Class1<T> where T : IComparable
    {

    }
    
    public interface IInterface<X, Y, Z> {
    
    	public String getName();
    
    }
    

}