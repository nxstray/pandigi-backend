package com.PPPL.backend.config;

import com.PPPL.backend.model.enums.KategoriLayanan;
import com.PPPL.backend.model.layanan.Layanan;
import com.PPPL.backend.repository.LayananRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LayananSeeder {

    @Bean
    CommandLineRunner seedLayanan(LayananRepository layananRepository) {
        return args -> {
            if (layananRepository.count() == 0) {

                layananRepository.save(new Layanan(
                    null,
                    "Website Development",
                    KategoriLayanan.PIRANTI_LUNAK,
                    "Pembuatan website perusahaan",
                    null,
                    null
                ));

                layananRepository.save(new Layanan(
                    null,
                    "Mobile App Development",
                    KategoriLayanan.PIRANTI_LUNAK,
                    "Aplikasi Android & iOS",
                    null,
                    null
                ));

                layananRepository.save(new Layanan(
                    null,
                    "Digital Marketing",
                    KategoriLayanan.SOSIAL,
                    "Strategi media sosial",
                    null,
                    null
                ));
            }
        };
    }
}
