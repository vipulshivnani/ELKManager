package ELK;

import java.net.URL;

import com.vmware.vim25.mo.Folder;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.ManagedEntity;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.VirtualMachine;

import CONFIG.SJSULAB;


public class AvailabiltyManager implements Runnable{

	Thread t1, t2;
	
	MyVM[] myvm = new MyVM[2];  //Array to store the VMs in Team vCenter
	MyHost[] myvHost = new MyHost[3]; //Array to store Hosts on Admin vCenter
	int count = 0;
	String[] hosts = new String[3]; //Array to store Hosts added to team vCenter
	private boolean sync = false;
	ServiceInstance si = null;
	ServiceInstance si1 = null;
	Folder rootFolder = null;
	Folder rootFolder1 = null;


	public void run() {
		try{
			synchronized(this){
				URL url = new URL(SJSULAB.getVCenterURL());
				URL url1 = new URL(SJSULAB.getVmwareHostURL());
				final ServiceInstance si = new ServiceInstance(
				        url, SJSULAB.getVCenterLogin(), SJSULAB.getVCenterPassword(), true);
				ServiceInstance si1  = new ServiceInstance(
				        url1, SJSULAB.getVmwareLogin(), SJSULAB.getVmwarePassword(), true);
				final Folder rootFolder = si.getRootFolder();
				Folder rootFolder1 = si1.getRootFolder();
				ManagedEntity[] mes;
				ManagedEntity[] vhosts;
				ManagedEntity[] mhs;
				while(true){
					if (count == 0){
		
					    mes = new InventoryNavigator(rootFolder).searchManagedEntities("VirtualMachine");
			
					    if(mes==null || mes.length==0)
					    {
					      System.out.println("No VMs found");
					      si.getServerConnection().logout();
					      return;
					    }
					    
					    int k = 0;
					    for(int i = 0; i < mes.length; i++)
					    {
					    	VirtualMachine vm = (VirtualMachine) mes[i];
					    	if(!vm.getName().contains("Template") && k < 2 && !vm.getName().contains("Win"))
					    		if(vm.getRuntime().getPowerState().toString().contains("poweredOn")){
					    			myvm[k] = new MyVM(vm.getName());
					    			//myvm[k].createSnapshot();
					    			k++;
					    	}
					    }
					    
					    vhosts = new InventoryNavigator(
					    		rootFolder).searchManagedEntities("HostSystem");
					    
					    for(int i = 0; i < vhosts.length; i++){
					    	hosts[i] = vhosts[i].getName();	
					    }
					    
					    mhs = new InventoryNavigator(
					    		rootFolder1).searchManagedEntities("VirtualMachine");
					    
					    if(mhs == null || mhs.length == 0)
					    {
					    	System.out.println("No Hosts found");
					    	si.getServerConnection().logout();
					    	return;
					    }
					    
					    k = 0;
					    for(int i = 0; i < mhs.length; i++)
					    {
					    	VirtualMachine vm = (VirtualMachine) mhs[i];
					    	if(vm.getName().contains("T03") && !vm.getName().contains("VC") && k < 3)
					    		if(vm.getRuntime().getPowerState().toString().contains("poweredOn")){
						    		myvHost[k] = new MyHost(vm.getName());
						    		//myvHost[k].createSnapshot();
						    		k++;
						    		if(k == 3)
						    			break;
					    		}
					    }
					    
					    t2 = new Thread(){
					    	public void run(){
					    		try{
					    			synchronized(this){
						    			while(true){
							    			Thread.sleep(1000); // 10 minutes sleep
							    			ManagedEntity[] mes;
							    			ManagedEntity[] vhosts;
							    			while(!sync){
							    				System.out.println("Waiting");
							    				wait();
							    			}
							    			System.out.println("Inside thread 2");
							    			mes = new InventoryNavigator(rootFolder).searchManagedEntities("VirtualMachine");
							    			
										    if(mes==null || mes.length==0)
										    {
										      System.out.println("No VMs found");
										      si.getServerConnection().logout();
										      return;
										    }
										    
										    int k = 0;
										    for(int i = 0; i < mes.length; i++)
										    {
										    	VirtualMachine vm = (VirtualMachine) mes[i];
										    	if(!vm.getName().contains("Template") && k < 2 && !vm.getName().contains("Win"))
										    		if(vm.getRuntime().getPowerState().toString().contains("poweredOn")){
										    			myvm[k] = new MyVM(vm.getName());
										    			myvm[k].createSnapshot();
										    			k++;
										    	}
										    }
										    
										    vhosts = new InventoryNavigator(
										    		rootFolder).searchManagedEntities("HostSystem");
										    
										    for(int i = 0; i < vhosts.length; i++){
										    	hosts[i] = vhosts[i].getName();	
										    }
										    sync = false;
						    			}	
					    			}
					    		}
					    		catch(Exception e){
					    			e.printStackTrace();
					    		}
					    	}
					    };
					    t2.start();
					    count++;
				    }
					
					
					for(int i = 0; i < myvm.length; i++){
						if(myvm[i] != null){
							myvm[i].pingVM();
							
							if(myvm[i].getStatus()){ // Virtual machine responded to ping
								System.out.println("VM " + myvm[i].getVmname() + " is up and running....");
								myvm[i].getUpdates();
							}
							else{ // Virtual machine not up... Check the vHost
								System.out.println("VM " + myvm[i].getVmname() + " is not responding.. checking its host");
								String temp = myvm[i].getHostname().substring(7);
								int tryping;
								
								System.out.println("The IP address of the host is " + myvm[i].getHostname());
								boolean result = myvm[i].pingHost();
								
								if(!result){ //Host did not respond to the ping
									System.out.println("Host " + myvm[i].getHostname() + " is not responding.. Trying to ping again");
									tryping = 3;
									
									while(tryping != 0){
										Thread.sleep(2000);
										result = myvm[i].pingHost();
										if(!result){
											tryping--;
										}
										else
											break;	
									}//while
											
									if(tryping == 0){ // Host failed
										System.out.println("Host did not respond.. Trying to revive using snapshot");
										for(int j = 0; j < myvHost.length; j++)
											if(myvHost[j] != null){
												if(myvHost[j].getHostname().contains(temp)){
													System.out.println("This will take some time...");
													myvHost[j].revertHosttoSnapshot();
													
													if(!myvHost[j].checkHostStatus())
													{
														System.out.println("Powering on " + myvm[i].getHostname());
														myvHost[j].powerOn();
														Thread.sleep(240000);
														myvHost[j].checkHostStatus();
														if(!myvm[i].checkVMStatus()){
															myvm[i].powerOn();
															Thread.sleep(240000);
														}
															
													}
													break;
												}
											}
													
										result = myvm[i].pingHost();
										if(result){
											System.out.println("Host " + myvm[i].getHostname() + " is up and responding now");
										}
										else{ //Host did not respond even after reverting to snapshot
											System.out.println("Host did not respond after reverting to snapshot.. Trying to look for other hosts on datacenter");
											String otherhostip = myvm[i].searchOtherHosts();
											if(otherhostip.isEmpty()){
												System.out.println("No other host found... Adding new host to datacenter");
												String host = checkAvailableHost();
												if(!host.isEmpty()){
													System.out.println("Found another host that can be added to the datacenter");												//otherhostip = myvm[i].addNewHost(host);
													otherhostip = myvm[i].addNewHost("130.65." + host);
													Thread.sleep(240000);
													if(!otherhostip.isEmpty()){
														System.out.println("Host " + otherhostip + " added successfully to datacenter");
														
														vhosts = new InventoryNavigator(
													    		rootFolder).searchManagedEntities("HostSystem");
													    
													    for(int m = 0; m < vhosts.length; m++){
													    	hosts[m] = vhosts[m].getName();	
													    }
													}
												}
												else
													System.out.println("No hosts are available to be added");
											}
											
											if(myvm[i].pingHost(otherhostip)){
											
												System.out.println("Migrating VM to host " + otherhostip + ". This will take some time...");
												
												if(myvm[i].checkVMStatus())
													myvm[i].powerOff();
												myvm[i].migrateVM(otherhostip);
												//myvm[i].powerOn();
												Thread.sleep(240000);
												myvm[i].checkVMStatus();
											}
										}
										myvm[i].pingVM();
										if(myvm[i].getStatus()){
											System.out.println("VM is up and running now");
										}
										
									}
									else{
										System.out.println("Host responded.. Host is running.. Checking alarm status");
										if(myvm[i].checkAlarmState()){
											System.out.println("The Virtual Machine is powered off.. The system has no problem");
										}
										else{
											System.out.println("The Virtual Machine was not powered off. Trying to revive machine using snapshot");
											System.out.println("This will take some time....");
											myvm[i].revertVMtoSnapshot();
											if(!myvm[i].checkVMStatus())
											{
												System.out.println("Powering On " + myvm[i].getVmname());
												myvm[i].powerOn();
											}
											Thread.sleep(240000);
											myvm[i].checkVMStatus();
											myvm[i].pingVM();
											if(myvm[i].getStatus()){
												System.out.println("VM is up and running now");
											}
										}
									}
								}//host failed to respond
								else{ //Host up, check alarm or revert to snapshot
									System.out.println("Host is running.. Checking alarm status");
									if(myvm[i].checkAlarmState()){
										System.out.println("The Virtual Machine is powered off.. The system has no problem");
									}
									else{
										System.out.println("The Virtual Machine was not powered off. Trying to revive machine using snapshot");
										System.out.println("This will take some time....");
										myvm[i].revertVMtoSnapshot();
										if(!myvm[i].checkVMStatus())
										{
											System.out.println("Powering On " + myvm[i].getVmname());
											myvm[i].powerOn();
											
										}
										
										Thread.sleep(240000);
										myvm[i].checkVMStatus();
										myvm[i].pingVM();
										if(myvm[i].getStatus()){
											System.out.println("VM is up and running now");
										}
									}
								}
							}//vm not responding
						}
					}
					sync = true;
					notify();
				    Thread.sleep(60000); //sleep for 1 minute before pinging
				} 
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}	
	}
	
	//Function to check the host that is not present on the vCenter
	private String checkAvailableHost() {
        boolean flag = false;
        int i, j;
        for(i = 0; i < myvHost.length; i++)
        	if(myvHost[i] != null){
	            for(j = 0; j < hosts.length; j++){
	            	if(hosts[j] != null && !hosts[j].isEmpty())
	            		if(myvHost[i].getHostname().contains(hosts[j].substring(7))){
	            			flag = true;
	            			break;
	            		}
	            }
	            if(flag == true)
	                flag = false;
	            else
	                return myvHost[i].getHostname().substring(myvHost[i].getHostname().lastIndexOf("132"));
        	}
        return "";
    }

	public void begin(){
		t1 = new Thread(this);
		t1.run();
	}
	
}
