package ELK;


import java.net.URL;

import CONFIG.SJSULAB;

import com.vmware.vim25.VirtualMachineRuntimeInfo;
import com.vmware.vim25.mo.Folder;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.Task;
import com.vmware.vim25.mo.VirtualMachine;
import com.vmware.vim25.mo.VirtualMachineSnapshot;

public class MyHost {
	private String hostname;
	private String snapshotname;
	private VirtualMachine hs;
	private Folder rootFolder;
	private ServiceInstance si;
	private boolean status;
	
	public MyHost(String name) {
		try{
			setHostname(name);
			snapshotname = name + "snapshot";
			this.si = new ServiceInstance(new URL(
					SJSULAB.getVmwareHostURL()), SJSULAB.getVmwareLogin(), SJSULAB.getVmwarePassword());
			rootFolder = si.getRootFolder();
			hs = (VirtualMachine) new InventoryNavigator(
					rootFolder).searchManagedEntity("VirtualMachine", getHostname());
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
	
	public String getHostname() {
		return hostname;
	}
	
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public void createSnapshot() {
		try{
			VirtualMachineSnapshot vmSnap= hs.getCurrentSnapShot();
			if(vmSnap !=null) {
				Task t = vmSnap.removeSnapshot_Task(true);
				if(t.waitForTask()==Task.SUCCESS)
				{
					System.out.println("Snapshot was deleted.");
				}
			}
			Task task = hs.createSnapshot_Task(
					snapshotname, "Creating snapshot", false, false);
			if(task.waitForTask()==Task.SUCCESS)
			{
				System.out.println("Snapshot was created.");
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}

	public void revertHosttoSnapshot() {
		try{
			Task task = hs.revertToCurrentSnapshot_Task(null);
			if(task.waitForTask() == Task.SUCCESS){
				System.out.println("Snapshot revertion for " + hs.getName() + " successful");
			}
			else{
				System.out.println("Host could not be reverted to snapshot");
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
	}

	public boolean checkHostStatus() {
			
		boolean powerstate = false;
			
		VirtualMachineRuntimeInfo hsri = hs.getRuntime();
			
		String state = hsri.getPowerState().toString();
			
		if(state.contains("poweredOn")){
			System.out.println("The host " + hs.getName() + " is powered on");
			powerstate = true;
		}
		return powerstate;
	}

	public void powerOn() {
		try {
			System.out.println("Powering on host '"+hs.getName() +"'. Please wait...");     
			Task t = hs.powerOnVM_Task(null);
			if(t.waitForTask()== Task.SUCCESS)
			{
				System.out.println("Host " + hs.getName() + " powered on.");
			} 
		}
		catch ( Exception e ) 
		{ 
			e.printStackTrace();; 
		}	
	}
	
}
