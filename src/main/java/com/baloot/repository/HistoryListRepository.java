package com.baloot.repository;

import com.baloot.model.HistoryList;
import com.baloot.model.id.HistoryListId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoryListRepository extends JpaRepository<HistoryList, HistoryListId> {
}
