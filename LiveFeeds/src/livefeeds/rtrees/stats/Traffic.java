package livefeeds.rtrees.stats;

import java.util.LinkedList;

import simsim.core.Simulation;

public class Traffic implements Cloneable {
	
	LinkedList<Traffic> history = new LinkedList<Traffic>() ;
	
	Traffic() {
		updateHistory() ; //
	}
	
	public double timeStamp = Simulation.currentTime() ;
	
	public double dbUploadEndpoints_upload = 0, dbUploadEndpoints_download = 0;
	public double dbUploadFilters_upload = 0, dbUploadFilters_download = 0;
	public double dbUploadReject_upload = 0, dbUploadReject_download = 0;
	public double dbUploadRequest_upload = 0, dbUploadRequest_download = 0;
	
	public double departure_upload = 0, departure_download = 0;
	public double joinRequest_upload = 0, joinRequest_download = 0;
	public double catadupaCasting_upload = 0, catadupaCasting_download = 0;
	
	public double dbRepairReply_upload = 0, dbRepairReply_download = 0;
	public double dbRepairRequest_upload = 0, dbRepairRequest_download = 0;
	
	
	public double repair_upload() {
		return dbRepairRequest_upload + dbRepairReply_upload ;
	}
	
	public double repair_download() {
		return dbRepairRequest_download + dbRepairReply_download ;
	}
	
	public double db_upload() {
		return dbUploadEndpoints_upload + dbUploadFilters_upload + dbUploadReject_upload + dbUploadRequest_upload ;
	}
	
	public double db_download() {
		return dbUploadEndpoints_download + dbUploadFilters_download + dbUploadReject_download + dbUploadRequest_download ;
	}
	
	public double casting_upload() {
		return joinRequest_upload + catadupaCasting_upload ;		
	}

	public double casting_download() {
		return joinRequest_download + catadupaCasting_download ;
	}

	public double endpoints_upload() {
		return dbUploadEndpoints_upload ;
	}

	public double endpoints_download() {
		return dbUploadEndpoints_download ;
	}


	public double filters_upload() {
		return dbUploadFilters_upload ;
	}

	public double filters_download() {
		return dbUploadFilters_download ;
	}

	public double departure_upload() {
		return departure_upload ;	
	}
	
	public double departure_download() {
		return departure_download ;
	}

	public double upload() {
		return repair_upload() + db_upload() + casting_upload() + departure_upload();
	}
	
	public double download() {
		return repair_download() + db_download() + casting_download() + departure_download();
	}
	
	public double recent_upload_rate() {
		
		Traffic other = history.getLast() ;
		
		double elapsed = Simulation.currentTime() - other.timeStamp ;
		double total = this.upload() - other.upload() ;
		
		return total / elapsed ;
	}
	
	public double recent_download_rate() {
		Traffic other = history.getLast() ;
		
		double elapsed = Simulation.currentTime() - other.timeStamp ;
		double total = this.download() - other.download() ;
		
		return total / elapsed ;
	}
	
	public double recent_repair_upload_rate() {
		Traffic other = history.getLast() ;
		
		double elapsed = Simulation.currentTime() - other.timeStamp ;
		double total = this.repair_upload() - other.repair_upload() ;
		
		return total / elapsed ;
	}
	
	public double recent_repair_download_rate() {
		Traffic other = history.getLast() ;
		
		double elapsed = Simulation.currentTime() - other.timeStamp ;
		double total = this.repair_download() - other.repair_download() ;
		
		return total / elapsed ;
	}
	
	public double recent_db_upload_rate() {
		Traffic other = history.getLast() ;
		
		double elapsed = Simulation.currentTime() - other.timeStamp ;
		double total = this.db_upload() - other.db_upload() ;
		
		return total / elapsed ;
	}
	
	public double recent_db_download_rate() {
		Traffic other = history.getLast() ;
		
		double elapsed = Simulation.currentTime() - other.timeStamp ;
		double total = this.db_download() - other.db_download() ;
		
		return total / elapsed ;
	}

	public double recent_filters_upload_rate() {
		Traffic other = history.getLast() ;
		
		double elapsed = Simulation.currentTime() - other.timeStamp ;
		double total = this.filters_upload() - other.filters_upload() ;
		
		return total / elapsed ;
	}
	
	public double recent_filters_download_rate() {
		Traffic other = history.getLast() ;
		
		double elapsed = Simulation.currentTime() - other.timeStamp ;
		double total = this.filters_download() - other.filters_download() ;
		
		return total / elapsed ;
	}

	public double recent_endpoints_upload_rate() {
		Traffic other = history.getLast() ;
		
		double elapsed = Simulation.currentTime() - other.timeStamp ;
		double total = this.endpoints_upload() - other.endpoints_upload();
		
		return total / elapsed ;
	}
	
	public double recent_endpoints_download_rate() {
		Traffic other = history.getLast() ;
		
		double elapsed = Simulation.currentTime() - other.timeStamp ;
		double total = this.endpoints_download() - other.endpoints_download();
		
		return total / elapsed ;
	}

	
	public double recent_casting_upload_rate() {
		
		Traffic other = history.getLast() ;
		
		double elapsed = Simulation.currentTime() - other.timeStamp ;
		double total = this.casting_upload() - other.casting_upload() ;
		
		return total / elapsed ;
	}
	
	public double recent_casting_download_rate() {
		Traffic other = history.getLast() ;
		
		double elapsed = Simulation.currentTime() - other.timeStamp ;
		double total = this.casting_download() - other.casting_download() ;
		
		return total / elapsed ;
	}

	public double recent_departure_upload_rate() {
		Traffic other = history.getLast() ;
		
		double elapsed = Simulation.currentTime() - other.timeStamp ;
		double total = this.departure_upload() - other.departure_upload() ;
		
		return total / elapsed ;
	}
	
	public double recent_departure_download_rate() {
		Traffic other = history.getLast() ;
		
		double elapsed = Simulation.currentTime() - other.timeStamp ;
		double total = this.departure_download() - other.departure_download() ;
		
		return total / elapsed ;
	}
	
	public void updateHistory() {

		history.addFirst( clone() ) ;
		while( history.size() > 3 )
			history.removeLast() ;
		
	}
	
	
	public Traffic clone() {
		try {
			timeStamp = Simulation.currentTime() ;			
			return (Traffic)super.clone() ;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace() ;
			return null ;
		}
	}
}
