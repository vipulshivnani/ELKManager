package ELK;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Calendar;

import com.vmware.vim25.ComputeResourceConfigSpec;
import com.vmware.vim25.HostConnectSpec;
import com.vmware.vim25.VirtualMachineCapability;
import com.vmware.vim25.VirtualMachineConfigInfo;
import com.vmware.vim25.VirtualMachineMovePriority;
import com.vmware.vim25.VirtualMachinePowerState;
import com.vmware.vim25.VirtualMachineRuntimeInfo;
import com.vmware.vim25.mo.ComputeResource;
import com.vmware.vim25.mo.Datacenter;
import com.vmware.vim25.mo.HostSystem;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.ManagedEntity;
import com.vmware.vim25.mo.Task;
import com.vmware.vim25.mo.VirtualMachine;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.Folder;
import com.vmware.vim25.mo.VirtualMachineSnapshot;

import CONFIG.*;

public class MyVM {
	private String vmname;
	private String snapshotname;
	private VirtualMachine vm;
	private ServiceInstance si;
	private Folder rootFolder;
	private boolean status;
	private String hostname;
	
	MyVM(String name){
		try{
			vmname = name;
			snapshotname = name + "_snapshot";
			this.si = new ServiceInstance(new URL(
					SJSULAB.getVCenterURL()), SJSULAB.getVCenterLogin(), SJSULAB.getVCenterPassword());
			rootFolder = si.getRootFolder();
			vm = (VirtualMachine) new InventoryNavigator(
					rootFolder).searchManagedEntity("VirtualMachine", getVmname());
			VirtualMachineRuntimeInfo rtime = vm.getRuntime();
			
			HostSystem hs = new HostSystem(vm.getServerConnection(),rtime.host);
			
			setHostname(hs.getName());
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	public String getVmname() {
		return vmname;
	}

	public void setVmname(String vmname) {
		this.vmname = vmname;
	}
	
	public void createSnapshot(){
		try{
			VirtualMachineSnapshot vmSnap= vm.getCurrentSnapShot();
			if(vmSnap !=null) {
				Task t = vmSnap.removeSnapshot_Task(true);
				if(t.waitForTask()==Task.SUCCESS)
				{
					System.out.println("Snapshot was deleted.");
				}
			}
			
			Task task = vm.createSnapshot_Task(
		          snapshotname, "Creating snapshot", false, false);
		    if(task.waitForTask()==Task.SUCCESS)
		    {
		    	System.out.println("Snapshot was created.");
		    }
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private boolean pingAll(String ip){
		boolean pingResult = false;

		String cmd = "ping " + ip;
		String result = null;
		
		try{
			if(ip != null){
				Runtime r = Runtime.getRuntime();
				Process p = r.exec(cmd);
				int count = 0;
				
				BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
				String output = input.readLine();
				
				result += output;
				while(count < 5)
				{
					System.out.println(output);
					result += output;
					output = input.readLine();
					count++;
				}
				
				if(!result.contains("Request timed out")){
					System.out.println("Ping Successful");
					pingResult = true;
				}
				else{
					System.out.println("Ping Failed");
					pingResult = false;
				}
				
			}
			else
			{
				System.out.println("IP not found");
				pingResult = false;
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		return pingResult;
	}
	
	public void pingVM(){
		setStatus(false);
		String ip;
		
		try{
			ip = vm.getGuest().ipAddress;
			
			setStatus(pingAll(ip));
			
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
	}

	public boolean getStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public void getUpdates() {
		VirtualMachineConfigInfo vminfo = vm.getConfig();
		VirtualMachineCapability vmc = vm.getCapability();
		VirtualMachineRuntimeInfo vmri = vm.getRuntime();
		
		Long mOverhead = vmri.getMemoryOverhead();

		System.out.println("GuestOS: " + vminfo.getGuestFullName());
		System.out.println("Multiple snapshot supported: " + vmc.isMultipleSnapshotsSupported());	
		System.out.println("Power Status: " + vm.getRuntime().getPowerState().toString());
		System.out.println("Connection Status: " + vm.getRuntime().getConnectionState().toString());

		if(mOverhead != null)
			System.out.println("Memory Overhead: " + mOverhead);
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	
	public void revertVMtoSnapshot() {
		try{
			Task task = vm.revertToCurrentSnapshot_Task(null);
			if(task.waitForTask() == Task.SUCCESS){
				System.out.println("Snapshot revertion for " + vm.getName() + " successful");
			}
			else{
				System.out.println("VM "+ vm.getName() + " could not be reverted to snapshot");
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
	}

	public boolean checkVMStatus() {
		
		boolean powerstate = false;
		VirtualMachineRuntimeInfo vmri = vm.getRuntime();
		
		String state = vmri.getPowerState().toString();
		
		if(state.contains("poweredOn")){
			System.out.println("VM " + vmname + " is powered on");
			powerstate = true;		
		}
		return powerstate;	
	}

	public boolean checkAlarmState() {
		boolean alarmState = true;
		
		if(vm.getTriggeredAlarmState() == null) //VM is not powered off
			alarmState = false;
		return alarmState;
	}

	public String searchOtherHosts() {
		String ip = "";
		try{
			ManagedEntity[] hosts = new InventoryNavigator(
					rootFolder).searchManagedEntities("HostSystem");
			if(hosts.length <= 1){
				System.out.println("No other host available on the vCenter");
			}
			else{
				for(int i = 0; i < hosts.length; i++){
					String hostIP = hosts[i].getName();
					if(!hostIP.equalsIgnoreCase(getHostname())){
						System.out.println("Found host " + hostIP);
						System.out.println("Trying to ping the host");
						boolean result = pingAll(hostIP);
						if(result){
							System.out.println("The host " + hostIP + "is alive");
							ip = hostIP;
							break;
						}
						else
							System.out.println("The host " + hostIP + "did not respond");
					}
				}
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return ip;
	}

	public String addNewHost(String host) {
		String hostIP = "";
		
		try{
			ManagedEntity [] mes =  new InventoryNavigator(rootFolder).searchManagedEntities("Datacenter");
			Datacenter dc = new Datacenter(rootFolder.getServerConnection(),  mes[0].getMOR());
			HostConnectSpec hs = new HostConnectSpec();
			
			hs.hostName= host;
			hs.userName ="root";
			hs.password = "12!@qwQW";
			
			if(host.contentEquals("130.65.132.152"))
	               hs.setSslThumbprint("47:01:6E:65:C6:DF:4C:7A:32:CD:91:48:1C:77:50:C7:DA:B4:3C:AE");
	        else if(host.contentEquals("130.65.132.153"))
	                hs.setSslThumbprint("55:3B:30:5B:CE:23:D1:5D:2B:46:BC:CA:0E:59:A4:D3:CC:5D:CA:17");

			ComputeResourceConfigSpec crcs = new ComputeResourceConfigSpec();
			Task task = dc.getHostFolder().addStandaloneHost_Task(hs,crcs, true);
			if(task.waitForTask() == Task.SUCCESS)
			{
				hostIP = host;
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return hostIP;
	}

	public void migrateVM(String hostip) {
		try{
			HostSystem host = (HostSystem)  new InventoryNavigator(
					rootFolder).searchManagedEntity("HostSystem", hostip);
			ComputeResource cr = (ComputeResource) host.getParent();

			Task task = vm.migrateVM_Task(cr.getResourcePool(), host, 
					VirtualMachineMovePriority.highPriority, VirtualMachinePowerState.poweredOff);
			
			if(task.waitForTask() == Task.SUCCESS){
				System.out.println("Migration successful");
				setHostname(hostip);
				powerOn();
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	public boolean pingHost() {
		boolean result = false;
		
		try{
			result = pingAll(hostname);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}
	
	public boolean pingHost (String hostip) {
		boolean result = false;
		
		try{
			System.out.println("The IP address of the host is " + hostip);
			
			result = pingAll(hostip);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}

	public void powerOn() {
		try {
				System.out.println("Powering on virtual machine '"+vm.getName() +"'. Please wait...");     
				Task t = vm.powerOnVM_Task(null);
				if(t.waitForTask()== Task.SUCCESS)
				{
					System.out.println("Virtual machine powered on.");
					
				} 
			}
		catch ( Exception e ) 
		{ 
			e.printStackTrace();; 
		}
		
	}

	public void powerOff() {
		try{
			System.out.println("Powering off virtual machine '"+vm.getName() +"'. Please wait...");     
			Task t = vm.powerOffVM_Task();
			if(t.waitForTask()== Task.SUCCESS)
			{
				System.out.println("Virtual machine powered off.");
			} 
			
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
	}

}
