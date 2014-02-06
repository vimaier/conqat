//-----------------------------------------------------------------------------
// <copyright file="ILAnalyzer.cs" company="Technische Universitaet Muenchen">
//     Licenced under Apache Licence.
// </copyright>
// <author>Martin Feilkas</author>
//-----------------------------------------------------------------------------

namespace Edu.Tum.Cs.Conqat.Dotnet.Il
{
    using System;
    using System.Collections.Generic;
    using System.IO;    
    using System.Xml;
    using Mono.Collections.Generic;
    using Mono.Cecil;
    using Mono.Cecil.Cil;
 
    /// <summary>
    /// This is the main class of the IL Analyzer providing the main method.
    /// It contains the methods used for the iteration over the syntax graph of the IL code as well as the output processing.
    /// </summary>
    public static class Analyzer
    {
        /// <summary>
        /// String constant that is used as argument for all dependencies that are directly associated with a type and cannot be associated with a member.
        /// </summary>
        private const string DirectTypeDependency = "";

        /// <summary>
        /// A representation of an IL file parsed using Cecil.
        /// </summary>
        private static AssemblyDefinition assemblyDefinition;
        
        /// <summary>
        /// Exclude member information from the output XML-file in order to reduce its size.
        /// </summary>
        private static Boolean excludeMembers = false;

        /// <summary>
        /// The path to the assembly that should be analyzed.
        /// </summary>
        private static string assemblyPath;

        /// <summary>
        /// The path to the output file.
        /// </summary>
        private static string xmlOutputPath = String.Empty;

        /// <summary>
        /// Constructs the XML output.
        /// </summary>
        private static XmlOutputWriter outputWriter = new XmlOutputWriter();

        /// <summary>
        /// Entry point: Runs the ILAnalyzer on the parameters stored in the configuration file.
        /// </summary>
        public static void Main(string[] args)
        {
            bool run = ParseCommandlineParameters(args);
            if (run)
            {
                Run();
            }
        }


        /// <summary>
        /// Opens the assembly and the XML-output file. Starts the iteration through all types. 
        /// </summary>
        public static void Run()
        {
            try
            {
                Console.Write("Running ILAnalyzer on {0} ... ", assemblyPath);
                XmlNode rootNode = outputWriter.AddAssemblyInformations(assemblyPath, assemblyDefinition);

                // Stait the iteration over the IL syntax graph
                VisitAssembly(rootNode);

                Console.WriteLine("finished.");

                outputWriter.SaveOutput(xmlOutputPath);
            }
            catch (Exception e)
            {
                Console.Error.WriteLine(e.Message);
                Console.Error.WriteLine(e.StackTrace);
            }
        }

        #region ParseCommandLineParameters
        /// <summary>
        /// Parses the command line arguments and prints error or warning messages.
        /// Initializes the ILAnalyzer
        /// </summary>
        /// <param name="args">The command line arguments</param>
        private static bool ParseCommandlineParameters(string[] args)
        {
            bool error = false;
            bool MissingAssemblyArgument = true;

            for (int i = 0; i < args.Length; i++)
            {

                if (error) break;

                switch (args[i].Trim())
                {
                    case "-out":
                        i++;
                        if (i < args.Length)
                        {
                            xmlOutputPath = args[i];
                            int index = xmlOutputPath.LastIndexOf('\\');
                            if (index > 0)
                            {
                                string directory = xmlOutputPath.Remove(index);
                                if (!Directory.Exists(directory))
                                {
                                    Console.WriteLine("WARNING: Specified output path \"{0}\" does not exist - trying to create it.", directory);
                                    try
                                    {
                                        Directory.CreateDirectory(directory);
                                    }
                                    catch (Exception e)
                                    {
                                        Console.WriteLine("ERROR: Failed creating directory.");
                                        Console.WriteLine(e.Message);
                                        error = true;
                                    }
                                }
                            } // else: only a file is specified so the file will be created in the working directory.
                        }
                        else
                        {
                            Console.WriteLine("ERROR: No valid output path specified.");
                            error = true;
                        }
                        break;
                    case "-in":
                        i++;
                        if (i < args.Length)
                        {
                            MissingAssemblyArgument = false;
                            if (!LoadAssembly(args[i]))
                            {
                                Console.WriteLine("{0} is not a valid .NET assembly", args[i]);
                                error = true;
                            }
                        }
                        else
                        {
                            error = true;
                        }
                        break;
                    case "-h":
                        printHelp();
                        MissingAssemblyArgument = false;
                        error = true;
                        break;
                    case "-excludeMembers":
                        excludeMembers = true;
                        break;
                    default:
                        Console.WriteLine("Unknown argument:  {0}\n\n", args[i]);
                        Console.WriteLine("Use -h options for help.");
                        error = true;
                        break;
                }
            }

            if (MissingAssemblyArgument)
            {
                Console.WriteLine("\nA .NET assembly must be specified using the -in option!");
                printHelp();
                error = true;
            }

            return !error;
        }

        /// <summary>
        /// Loads the assembly using Cecil.
        /// </summary>
        /// <param name="path">The path to the assembly.</param>
        /// <returns>A boolean value that signalizes if the load was successful.</returns>
        private static bool LoadAssembly(string path)
        {
            bool success = false;
            assemblyPath = path;
            if (!File.Exists(assemblyPath))
            {
                Console.WriteLine("ERROR: Specified assembly does not exist.");
                return false;
            }

            // Check if a pdb file exists
            string pdbPath = assemblyPath.Remove(assemblyPath.LastIndexOf('.')) + ".pdb";
            if (!File.Exists(pdbPath))
            {
                Console.Write("WARNING: No .pdb file found named: {0} ", pdbPath);
                outputWriter.printSourceInformation = false;
            }

            // Load the debug symbols - needed to retrieve the source code file and line numbers
            try
            {
                var readerParameters = new ReaderParameters { ReadSymbols = true };
                assemblyDefinition = AssemblyDefinition.ReadAssembly(assemblyPath, readerParameters);
            }
            catch (FileNotFoundException e)
            {
                outputWriter.printSourceInformation = false;
                Console.WriteLine("(could not load debug informations) ... ");
                // Try to load the assembly without symbols
                try
                {
                    assemblyDefinition = AssemblyDefinition.ReadAssembly(assemblyPath);
                }
                catch (Exception ex)
                {
                    Console.WriteLine("ERROR: Could not load assembly. Please check if the specified file is a valid .NET assembly: {0}", assemblyPath);
                    Console.WriteLine(ex.Message);
                    return false;
                }
            }
            

            if (assemblyDefinition == null)
            {
                Console.WriteLine("ERROR: The assembly could not be loaded: {0}", assemblyPath);
            }
            else
            {
                success = true;
            }


            return success;
        }

        /// <summary>
        /// Prints the usage information to the console.
        /// </summary>
        private static void printHelp()
        {

            Console.WriteLine();
            Console.WriteLine("Usage:  ILAnalyzer <options>");
            Console.WriteLine();

            Console.WriteLine("Options: ");
            Console.WriteLine();

            Console.WriteLine("-in <IL file>                 Path to an executable IL file (.dll or. exe)");
            Console.WriteLine("-out <path to output>         Path to the output location");
            Console.WriteLine("-excludeMembers               Exclude the members of the types in the output XML in order to reduce the file size");
            Console.WriteLine("-h                            Print help page");
        }
        #endregion

        #region TraverseAST

        /// <summary>
        /// The starting point for the depth-first search over the IL-tree.
        /// Loads the debug informations.
        /// </summary>
        /// <param name="parentNode">The XmlNode to which the output should be attached.</param>
        private static void VisitAssembly(XmlNode parentNode)
        {
            foreach (ModuleDefinition moduleDefinition in assemblyDefinition.Modules)
            {
                VisitModule(parentNode, moduleDefinition);
            }
        }

        /// <summary>
        /// Iterates over all types in the module and attaches the type- and dependency informations on into the XML output.
        /// </summary>
        /// <param name="parentNode">The XmlNode to which the output will be attached.</param>
        /// <param name="moduleDefinition">The module that should be investigated. </param>
        private static void VisitModule(XmlNode parentNode, ModuleDefinition moduleDefinition)
        {

            outputWriter.AddAssemblyReferences(parentNode, moduleDefinition.AssemblyReferences);

            foreach (TypeDefinition typeDefinition in moduleDefinition.Types)
            {

                // Visit the type to retrieve its dependencies
                VisitType(typeDefinition, parentNode);

            }
        }

        /// <summary>
        /// Collects the dependency information for a given TypeDefinition.
        /// </summary>
        /// <param name="typeDef">The TypeDefinition to investigate.</param>
        /// <param name="parentNode">The XmlNode to which the output will be attached.</param>       
        private static void VisitType(TypeDefinition typeDef, XmlNode parentNode)
        {
            // prepare a collection for the dependencies of the typeDefintion
            TypeDependencyCollection dependencies = new TypeDependencyCollection(typeDef);

            SourceFile sourceFile = new SourceFile();
            VisitAttributes(typeDef.CustomAttributes, dependencies, sourceFile);
            VisitGenericParameters(typeDef.GenericParameters, dependencies, sourceFile);
           
            // visit all members
            foreach (MethodDefinition method in typeDef.Methods) // also Constructors and Properties reside in this collection
            {
                DependencyCollection methDependencies = RetrieveAppropriateDependencyCollection(dependencies, method, MemberType.Method);
                VisitMethod(method, methDependencies);
            }
            foreach (FieldDefinition field in typeDef.Fields)
            {
                DependencyCollection fieldDependencies = RetrieveAppropriateDependencyCollection(dependencies, field, MemberType.Field);
                VisitAttributes(field.CustomAttributes, fieldDependencies, sourceFile);
                InsertDependentTypes(field.FieldType, fieldDependencies, sourceFile);
            }
            foreach (EventDefinition eventDef in typeDef.Events)
            {
                DependencyCollection eventDependencies = RetrieveAppropriateDependencyCollection(dependencies, eventDef, MemberType.Event);
                VisitAttributes(eventDef.CustomAttributes, eventDependencies, sourceFile);
                InsertDependentTypes(eventDef.EventType, eventDependencies, sourceFile);
            }

            // Write the output for the given type
            outputWriter.AddXmlForTypeDefinition(parentNode, dependencies);

            foreach (TypeDefinition nestedType in typeDef.NestedTypes)
            {
                VisitType(nestedType, parentNode);
            }
        }

        /// <summary>
        /// Returns a DependencyCollection suitable to the -excludeMembers command line flag (the DependencyCollection of the TypeDefinition or a new MemberDependencyCollection for the member).
        /// </summary>
        /// <param name="dependencies">The DependencyCollection of the TypeDeclaration</param>
        /// <param name="member">The member of the TypeDeclaration.</param>
        /// <param name="memberType">The kind of member to be dealt with.</param>
        /// <returns></returns>
        private static DependencyCollection RetrieveAppropriateDependencyCollection(TypeDependencyCollection dependencies, IMemberDefinition member, MemberType memberType)
        {
            DependencyCollection memberDependencies = dependencies;
            if (!excludeMembers)
            {
                memberDependencies = dependencies.AddMemberDependencyCollection(member, memberType);
            }
            return memberDependencies;
        }


        /// <summary>
        /// Collects the dependency information for a collection of attributes (as found in the context of a TypeDefinition or its members).
        /// </summary>
        /// <param name="attributes">The collection of attributes to investigate.</param>
        /// <param name="dependencies">The collection of dependencies into which all found dependencies are inserted. (Can be regared as an out-parameter.)</param>
        /// <param name="sourceFile">The corresdonding source code location.</param>
        private static void VisitAttributes(Collection<CustomAttribute> attributes, DependencyCollection dependencies, SourceFile sourceFile)
        {
            foreach (CustomAttribute attribute in attributes)
            {

                InsertDependentTypes(attribute.Constructor.DeclaringType, dependencies, sourceFile);
                try
                {
                    // include type parameters into the dependency collection
                    for (int i = 0; i < attribute.Constructor.Parameters.Count; i++)
                    {
                        ParameterDefinition param = attribute.Constructor.Parameters[i];
                        InsertDependentTypes(param.ParameterType, dependencies, sourceFile);

                        if (attribute.ConstructorArguments.Count == attribute.Constructor.Parameters.Count)
                        {

                            // the constructor parameters are Type-parameters (using typeof())
                            VisitAttributeParameters(dependencies, sourceFile, param.ParameterType.FullName, attribute.ConstructorArguments[i].Value);
                        }
                        else
                        {
                            // the constructor parameters are Enum- or constant-values
                        }

                    }

                    // investigate name-value parameters
                    foreach (CustomAttributeNamedArgument field in attribute.Fields)
                    {
                        TypeReference fieldType = field.Argument.Type;
                        InsertDependentTypes(fieldType, dependencies, sourceFile);
                        VisitAttributeParameters(dependencies, sourceFile, fieldType.FullName, field.Argument.Value);
                    }

                    foreach (CustomAttributeNamedArgument property in attribute.Properties)
                    {
                        TypeReference propertyType = property.Argument.Type;
                        InsertDependentTypes(propertyType, dependencies, sourceFile);
                        VisitAttributeParameters(dependencies, sourceFile, propertyType.FullName, property.Argument.Value);
                    }
                }
                catch (Mono.Cecil.AssemblyResolutionException e)
                {
                    // In some cases Cecil tries to load an assembly when accessing the arguments of a CustomAttribute
                    // This cases this exception, see Bug #3633
                    // We log it and continue with the analysis ...
                    Console.WriteLine("\nWARNING: Cecil Assembly Resolution failed - perhaps some dependencies are missing. Assembly: {0}", e.AssemblyReference);
                }
            }
        }

        /// <summary>
        /// This method adds all depencencies of a typeof()-argument of an attribute to the dependencies of the type.
        /// </summary>
        /// <param name="dependencies">The collection of dependencies into which all found dependencies are inserted. (Can be regared as an out-parameter.)</param>
        /// <param name="sourceFile">The corresdonding source code location.</param>
        /// <param name="parameterType">A string representing the type of the parameter.</param>
        /// <param name="parameterValue">The argument that is passed for this attribute parameter.</param>
        private static void VisitAttributeParameters(DependencyCollection dependencies, SourceFile sourceFile, string parameterType, object parameterValue)
        {
            if (parameterType.Equals("System.Type"))
            {
                // In the case of external types IL code contains the informations on the assembly defining the type
                String[] typeParts = parameterValue.ToString().Split(',');
                string cleanedDependency = typeParts[0].Replace("+", "/");

                // Remove terminating "\0"
                // Bug ID 2268: Cecil sometimes terminates constructor parameters in attributes that use typeof() with "\0". The reason therefore is unclear. 
                if (cleanedDependency.EndsWith("\0"))
                {
                    cleanedDependency = cleanedDependency.Remove(cleanedDependency.Length - 2);
                }

                if (!cleanedDependency.Equals(String.Empty))
                {
                    dependencies.AddDependency(cleanedDependency, DirectTypeDependency, sourceFile);
                }
            }
            else if (parameterType.Equals("System.Type[]"))
            {
                foreach (CustomAttributeArgument arrayMember in (CustomAttributeArgument[])parameterValue)
                {
                    string memberType = arrayMember.Value.ToString();
                    // In the case of external types IL code contains the informations on the assembly defining the type
                    string[] typeParts = memberType.Split(',');
                    string cleanedDependency = typeParts[0].Replace("+", "/");
                    if (!cleanedDependency.Equals(string.Empty))
                    {
                        dependencies.AddDependency(cleanedDependency, DirectTypeDependency, sourceFile);
                    }
                }
            }
        }

        /// <summary>
        /// Collects the dependency information for a given MethodDefinition.
        /// </summary>
        /// <param name="methodDef">The MethodDefinition to investigate.</param>
        /// <param name="dependencies">The collection of dependencies into which all found dependencies are inserted. (Can be regared as an out-parameter.)</param>
        private static void VisitMethod(MethodDefinition methodDef, DependencyCollection dependencies)
        {
            SourceFile sourceFile = GetSourceFileForMethod(methodDef);
            VisitAttributes(methodDef.CustomAttributes, dependencies, sourceFile);
            VisitGenericParameters(methodDef.GenericParameters, dependencies, sourceFile);
            VisitParameters(methodDef.Parameters, dependencies, sourceFile);

            if (!methodDef.IsConstructor)
            {
                InsertDependentTypes(methodDef.MethodReturnType.ReturnType, dependencies, sourceFile);
            }
            if (methodDef.Body != null) // Methods in interfaces do not contain a body
            {
                VisitStatementBlock(methodDef.Body.Instructions, dependencies);
            }
        }


        /// <summary>
        /// Collects the dependency information for a given GenericParameterCollection.
        /// The parameter definition itself does not contain any depencencies, only the constraints for generic parameters may contain dependencies.
        /// </summary>
        /// <param name="parameters">The GenericParameterCollection to investigate.</param>
        /// <param name="dependencies">The collection of dependencies into which all found dependencies are inserted. (Can be regared as an out-parameter.)</param>
        /// <param name="sourceFile">The corresdonding source code location.</param>
        private static void VisitGenericParameters(Collection<GenericParameter> parameters, DependencyCollection dependencies, SourceFile sourceFile)
        {
            // only the types metioned in the constraints are of importance
            foreach (GenericParameter param in parameters)
            {
                foreach (TypeReference constraint in param.Constraints)
                {
                    dependencies.AddDependency(constraint.FullName, DirectTypeDependency, sourceFile);
                    VisitGenericParameters(constraint.GenericParameters, dependencies, sourceFile);

                }
            }
        }

        /// <summary>
        /// Collects dependencies in generic arguments of fields, methods, events, classes, ...
        /// </summary>
        /// <param name="arguments">The GenericArgumentCollection to investigate.</param>
        /// <param name="dependencies">The collection of dependencies into which all found dependencies are inserted. (Can be regared as an out-parameter.)</param>
        /// <param name="sourceFile">The corresdonding source code location.</param>
        private static void VisitGenericArguments(Collection<TypeReference> arguments, DependencyCollection dependencies, SourceFile sourceFile)
        {
            foreach (TypeReference argument in arguments)
            {
                InsertDependentTypes(argument, dependencies, sourceFile);
            }
        }

        /// <summary>
        /// Collects the dependency information contained in parameter lists (e.g. the parameters of a method).
        /// </summary>
        /// <param name="parameters">The ParameterDefinitionCollection to investigate.</param>
        /// <param name="dependencies">The collection of dependencies into which all found dependencies are inserted. (Can be regared as an out-parameter.)</param>
        /// <param name="sourceFile">The corresdonding source code location.</param>
        private static void VisitParameters(Collection<ParameterDefinition> parameters, DependencyCollection dependencies, SourceFile sourceFile)
        {
            foreach (ParameterDefinition paraDef in parameters)
            {
                if (!IsGenericTypeVariable(paraDef.ParameterType))
                {
                    dependencies.AddDependency(paraDef.ParameterType.FullName, DirectTypeDependency, sourceFile);
                }

                VisitGenericParameters(paraDef.ParameterType.GenericParameters, dependencies, sourceFile);
            }
        }

        /// <summary>
        /// Checks if the TypeReference is a GenericTypeVariable (with are often called "T")
        /// </summary>
        /// <param name="type">The TypeReference to be investigated.</param>
        /// <returns></returns>
        private static bool IsGenericTypeVariable(TypeReference type)
        {

            if (type is GenericParameter)
            {
                return true;
            }
            if (type is ArrayType)
            {
                if (((ArrayType)type).ElementType is GenericParameter)
                {
                    return true;
                }

            }

            // if the type is a reference to a type specification also check whether this specification is a generic (= identifier, e.g. "T")
            // refer to test: NoDependency_genericClassParameterWithMethodParameterReference.cs
            if (type is TypeSpecification)
            {
                var typeSpec = ((TypeSpecification)type).ElementType;
                if (typeSpec is GenericParameter)
                {
                    return true;
                }

                if (typeSpec is ArrayType)
                {
                    if (((ArrayType)typeSpec).ElementType is GenericParameter)
                    {
                        return true;
                    }

                }
            }

            return false;
        }

        /// <summary>
        /// Collects the dependency information contained in a statement block (e.g. the statement block of a method).
        /// </summary>
        /// <param name="instructions">The InstructionCollection to investigate.</param>
        /// <param name="dependencies">The collection of dependencies into which all found dependencies are inserted. (Can be regared as an out-parameter.)</param>
        private static void VisitStatementBlock(Collection<Instruction> instructions, DependencyCollection dependencies)
        {
            foreach (Instruction instruction in instructions)
            {
                // Visit the instruction
                VisitInstruction(instruction, dependencies);
            }
        }

        /// <summary>
        /// Collects the dependency information contained in a single IL instruction.
        /// </summary>
        /// <param name="instruction">The Instruction to investigate.</param>
        /// <param name="dependencies">The collection of dependencies into which all found dependencies are inserted. (Can be regared as an out-parameter.)</param>
        private static void VisitInstruction(Instruction instruction, DependencyCollection dependencies)
        {
            if (!IsValidOperandType(instruction.OpCode.OperandType))
            {
                return;
            }

            if (instruction.Operand != null)
            {
                SourceFile sourceFile = new SourceFile(instruction.SequencePoint);
                if (instruction.Operand is GenericParameter)
                {
                    return;
                }
                else if (instruction.Operand is GenericInstanceType)
                {
                    GenericInstanceType instType = (GenericInstanceType)instruction.Operand;
                    VisitGenericArguments(instType.GenericArguments, dependencies, sourceFile);
                    dependencies.AddDependency(instType.FullName, DirectTypeDependency, sourceFile);
                }
                else if (instruction.Operand is TypeReference)
                {
                    TypeReference typeRef = (TypeReference)instruction.Operand;
                    VisitGenericParameters(typeRef.GenericParameters, dependencies, sourceFile);
                    if (!IsGenericTypeVariable(typeRef))
                    {
                        dependencies.AddDependency(typeRef.FullName, DirectTypeDependency, sourceFile);
                    }
                }
                else if (instruction.Operand is MemberReference)
                {
                    MemberReference memberRef = (MemberReference)instruction.Operand;
                    VisitMemberReference(dependencies, sourceFile, memberRef);
                }
                else if (instruction.Operand is CallSite)
                {
                    // Calls to non-managed code - currently not supported
                }
                else
                {   // This should never happen:
                    Console.WriteLine("\n\n Strange Operand found: {0}\n", instruction.Operand);
                }
            }
            else return;
        }


        /// <summary>
        /// Collects the dependencies from an instruction that has a MemberReference as operand.
        /// </summary>
        /// <param name="dependencies">The collection of dependencies into which all found dependencies are inserted. (Can be regared as an out-parameter.)</param>
        /// <param name="sourceFile">The corresdonding source code location.</param>
        /// <param name="memberRef">The MemberReference to investigate.</param>
        private static void VisitMemberReference(DependencyCollection dependencies, SourceFile sourceFile, MemberReference memberRef)
        {
            if (memberRef.DeclaringType != null)
            {
                string memberName = memberRef.Name;
                if (memberRef is MethodReference)
                {
                    memberName = Formatter.FormatMethodReference(((MethodReference)memberRef));
                }

                dependencies.AddDependency(memberRef.DeclaringType.FullName, memberName, sourceFile);
            }

            if (memberRef.DeclaringType is GenericInstanceType)
            {
                GenericInstanceType declaringType = (GenericInstanceType)memberRef.DeclaringType;
                VisitGenericArguments(declaringType.GenericArguments, dependencies, sourceFile);
            }

            if (memberRef is MethodReference)
            {
                VisitMethodReference(dependencies, sourceFile, (MethodReference)memberRef);
            }
            else if (memberRef is FieldReference)
            {
                VisitFieldReference(dependencies, sourceFile, (FieldReference)memberRef);
            }
        }

        /// <summary>
        /// Collects the dependencies form an instruction that has a MethodReference as operand.
        /// </summary>
        /// <param name="dependencies">The collection of dependencies into which all found dependencies are inserted. (Can be regared as an out-parameter.)</param>
        /// <param name="sourceFile">The corresdonding source code location.</param>
        /// <param name="methRef">The MethodReference to investigate.</param>
        private static void VisitMethodReference(DependencyCollection dependencies, SourceFile sourceFile, MethodReference methRef)
        {
            InsertDependentTypes(methRef.MethodReturnType.ReturnType, dependencies, sourceFile);

            VisitParameters(methRef.Parameters, dependencies, sourceFile);
            if (methRef is GenericInstanceMethod)
            {
                GenericInstanceMethod genMethod = (GenericInstanceMethod)methRef;
                VisitGenericArguments(genMethod.GenericArguments, dependencies, sourceFile);
            }
        }

        /// <summary>
        /// Collects the dependencies form an instruction that has a FieldReference as operand.
        /// </summary>
        /// <param name="dependencies">The collection of dependencies into which all found dependencies are inserted. (Can be regared as an out-parameter.)</param>
        /// <param name="sourceFile">The corresdonding source code location.</param>
        /// <param name="fieldRef">The FieldReference to investigate.</param>
        private static void VisitFieldReference(DependencyCollection dependencies, SourceFile sourceFile, FieldReference fieldRef)
        {
            InsertDependentTypes(fieldRef.FieldType, dependencies, sourceFile);
        }

        /// <summary>
        /// Checks if an OperandType contains dependency information.
        /// </summary>
        /// <param name="opType">The OperandType to investigate</param>
        /// <returns>True if the OperandType contains dependency information.</returns>
        private static bool IsValidOperandType(OperandType opType)
        {
            switch (opType)
            {
                case OperandType.ShortInlineI:
                case OperandType.ShortInlineR:
                case OperandType.InlineString:
                case OperandType.InlinePhi:
                case OperandType.ShortInlineArg:
                case OperandType.InlineNone:
                case OperandType.InlineR:
                case OperandType.InlineI:
                case OperandType.InlineI8:
                case OperandType.InlineVar:
                case OperandType.ShortInlineVar:
                case OperandType.InlineBrTarget:
                case OperandType.ShortInlineBrTarget:
                case OperandType.InlineSwitch:

                    return false;
                case OperandType.InlineTok:
                case OperandType.InlineMethod:
                case OperandType.InlineType:
                case OperandType.InlineSig:
                case OperandType.InlineField:
                    return true;
                default:
                    return false;
            }
        }

        /// <summary>
        /// Returns a SourceFile that constains the source code location of the method.
        /// (The first instruction of the method.)
        /// </summary>
        /// <param name="method">The method to investigate.</param>
        /// <returns>A SourceFile representing the source code location.</returns>
        internal static SourceFile GetSourceFileForMethod(MethodDefinition method)
        {
            SourceFile sourceFile = new SourceFile();
            if (method.Body == null) { return sourceFile; }
            if (method.Body.Instructions.Count <= 0) { return sourceFile; }
            if (method.Body.Instructions[0].SequencePoint == null) { return sourceFile; }

            sourceFile.Path = method.Body.Instructions[0].SequencePoint.Document.Url;
            sourceFile.Lines.Add(method.Body.Instructions[0].SequencePoint.StartLine.ToString());

            return sourceFile;
        }

        private static void InsertDependentTypes(TypeReference typeRef, DependencyCollection dependencies, SourceFile sourceFile)
        {
            if (!IsGenericTypeVariable(typeRef))
            {
                dependencies.AddDependency(typeRef.FullName, DirectTypeDependency, sourceFile);
            }

            if (typeRef is GenericInstanceType)
            {
                VisitGenericArguments(((GenericInstanceType)typeRef).GenericArguments, dependencies, sourceFile);
            }
        }

        #endregion

    } // end class ILAnalyzer
} // end namespace