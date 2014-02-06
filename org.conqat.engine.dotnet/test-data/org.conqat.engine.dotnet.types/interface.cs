public interface IInterface     {
        IArgs Args { get; }

        T GetService<T>() where T : class;
}