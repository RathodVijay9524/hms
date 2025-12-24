package com.vijay.User_Master.dto;
import com.vijay.User_Master.entity.Worker;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FavouriteEntryResponse {

    private Long id;

    private WorkerResponse worker;

    private Long userId;
}
