package tn.vulntrack.scan_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.vulntrack.scan_service.model.Scan;

public interface ScanRepository extends JpaRepository<Scan, String> {
}