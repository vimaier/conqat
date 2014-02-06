int fun(int a)
{
     int result = 0;
     char *buffer = kmalloc(SIZE);
 
     if (buffer == NULL)
             return -ENOMEM;
 
     if (condition1) {
             while (loop1) {
                     // ...
             }
             result = 1;
             goto out;
     }
     // ...
out:
     kfree(buffer);
     return result;
}

