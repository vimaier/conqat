
namespace TestConfiguration.Mocks
{
  public class MockConfigurationProvider : IConfigurationProvider
  {

		// Interesting language feature: event
		public event EventHandler Changed { 
			add 
      		{
         		eventTable["Event6"] = (MyDelegate4)eventTable["Event6"] + value;
      		}
      		remove
      		{
         		eventTable["Event6"] = (MyDelegate4)eventTable["Event6"] - value; 
      		}
      	}
  }
}
