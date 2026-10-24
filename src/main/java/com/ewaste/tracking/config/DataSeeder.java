package com.ewaste.tracking.config;

import com.ewaste.tracking.entity.*;
import com.ewaste.tracking.entity.builder.EWasteItemBuilder;
import com.ewaste.tracking.enums.EntityType;
import com.ewaste.tracking.enums.EWasteStatus;
import com.ewaste.tracking.repository.*;
import com.ewaste.tracking.service.TrackingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Seeds baseline demonstrative data into the in-memory H2 database on platform startup.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataSeeder.class);

    private final UserRepository userRepository;
    private final ConsumerRepository consumerRepository;
    private final FacilityStaffRepository staffRepository;
    private final BusinessUserRepository businessUserRepository;
    private final AdminUserRepository adminUserRepository;
    private final RecyclingFacilityRepository facilityRepository;
    private final DropOffPointRepository dropOffPointRepository;
    private final EWasteCategoryRepository categoryRepository;
    private final EWasteItemRepository ewasteItemRepository;
    private final TrackingService trackingService;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository,
                      ConsumerRepository consumerRepository,
                      FacilityStaffRepository staffRepository,
                      BusinessUserRepository businessUserRepository,
                      AdminUserRepository adminUserRepository,
                      RecyclingFacilityRepository facilityRepository,
                      DropOffPointRepository dropOffPointRepository,
                      EWasteCategoryRepository categoryRepository,
                      EWasteItemRepository ewasteItemRepository,
                      TrackingService trackingService,
                      PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.consumerRepository = consumerRepository;
        this.staffRepository = staffRepository;
        this.businessUserRepository = businessUserRepository;
        this.adminUserRepository = adminUserRepository;
        this.facilityRepository = facilityRepository;
        this.dropOffPointRepository = dropOffPointRepository;
        this.categoryRepository = categoryRepository;
        this.ewasteItemRepository = ewasteItemRepository;
        this.trackingService = trackingService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            logger.info("Database already seeded. Skipping initial seeding.");
            return;
        }

        logger.info("Starting initial seed data population for E-Waste Tracking Platform...");

        // 1. Seed Categories
        EWasteCategory laptop = categoryRepository.save(new EWasteCategory("Laptop", "Portable computers, ultrabooks, and notebooks"));
        EWasteCategory smartphone = categoryRepository.save(new EWasteCategory("Smartphone", "Mobile cellular phones, phablets, and tablets"));
        EWasteCategory battery = categoryRepository.save(new EWasteCategory("Battery", "Lithium-ion, NiMH, and power storage units"));
        EWasteCategory tv = categoryRepository.save(new EWasteCategory("TV", "Television sets, OLED/LCD displays, and desktop monitors"));
        EWasteCategory other = categoryRepository.save(new EWasteCategory("Other", "Cables, chargers, peripherals, and miscellaneous micro-electronics"));

        // 2. Seed Recycling Facility & Drop-Off Points
        RecyclingFacility greenTechFacility = facilityRepository.save(
                new RecyclingFacility("GreenTech Recycling Center", "45 Eco Boulevard, San Francisco, CA", 37.7749, -122.4194, true)
        );
        RecyclingFacility bayRecoveryFacility = facilityRepository.save(
                new RecyclingFacility("Bay Circular Recovery Hub", "1280 Valencia St, San Francisco, CA", 37.7520, -122.4220, true)
        );

        DropOffPoint dropPointA = dropOffPointRepository.save(
                new DropOffPoint(
                        greenTechFacility,
                        "Downtown Public Drop Box A",
                        37.7750,
                        -122.4180,
                        "100 Market St (Plaza Entrance), San Francisco, CA",
                        "https://images.unsplash.com/photo-1532996122724-e3c354a0b15b?auto=format&fit=crop&w=800&q=80",
                        "24/7 Smart Automated Drop Box",
                        "+1 (415) 555-0142",
                        "Smartphones, Tablets, Batteries, Cables, Chargers"
                )
        );
        DropOffPoint dropPointB = dropOffPointRepository.save(
                new DropOffPoint(
                        greenTechFacility,
                        "GreenTech Intake Bay B",
                        37.7749,
                        -122.4194,
                        "45 Eco Boulevard, Suite 100, San Francisco, CA",
                        "https://images.unsplash.com/photo-1581092160607-ee22621dd758?auto=format&fit=crop&w=800&q=80",
                        "Mon - Sat: 8:00 AM - 6:00 PM",
                        "+1 (415) 555-0199",
                        "All Categories (Laptops, Desktops, Servers, Large Displays)"
                )
        );
        DropOffPoint dropPointC = dropOffPointRepository.save(
                new DropOffPoint(
                        greenTechFacility,
                        "Westside Community E-Kiosk",
                        37.7600,
                        -122.4200,
                        "742 Mission St, San Francisco, CA",
                        "https://images.unsplash.com/photo-1591195853828-11db59a44f6b?auto=format&fit=crop&w=800&q=80",
                        "Daily: 7:00 AM - 9:00 PM",
                        "+1 (415) 555-0188",
                        "Smartphones, Laptops, Audio Gear, Small Appliances"
                )
        );
        DropOffPoint dropPointD = dropOffPointRepository.save(
                new DropOffPoint(
                        bayRecoveryFacility,
                        "Mission Express Intake Depot",
                        37.7520,
                        -122.4220,
                        "1280 Valencia St, San Francisco, CA",
                        "https://images.unsplash.com/photo-1526374965328-7f61d4dc18c5?auto=format&fit=crop&w=800&q=80",
                        "Mon - Fri: 9:00 AM - 5:30 PM",
                        "+1 (415) 555-0176",
                        "Laptops, Gaming Consoles, Batteries, Peripherals"
                )
        );

        // 3. Seed Users (Admin, Consumers, Facility Staff, Business User)
        AdminUser admin = adminUserRepository.save(
                new AdminUser("System Administrator", "admin@ewaste.org", passwordEncoder.encode("admin123"), "Platform Operations & Compliance")
        );

        Consumer alice = consumerRepository.save(
                new Consumer("Alice Green", "alice@example.com", passwordEncoder.encode("pass123"), 100)
        );
        Consumer bob = consumerRepository.save(
                new Consumer("Bob Clean", "bob@example.com", passwordEncoder.encode("pass123"), 50)
        );

        FacilityStaff staff = staffRepository.save(
                new FacilityStaff("John Tech", "staff@greentech.org", passwordEncoder.encode("staff123"), greenTechFacility)
        );

        BusinessUser business = businessUserRepository.save(
                new BusinessUser("Sarah Refurb", "contact@circularelectronics.com", passwordEncoder.encode("biz123"),
                        "Circular Electronics Inc.", "Hardware Refurbisher & Repair Network")
        );

        // 4. Seed 2 Sample EWasteItems in SUBMITTED status using the hand-written Builder
        EWasteItem item1 = new EWasteItemBuilder()
                .consumer(alice)
                .category(laptop)
                .dropOffPoint(dropPointA)
                .deviceDescription("Dell XPS 15 laptop with faulty motherboard; screen, RAM, and SSD intact")
                .status(EWasteStatus.SUBMITTED)
                .submittedAt(LocalDateTime.now().minusHours(3))
                .build();
        EWasteItem savedItem1 = ewasteItemRepository.save(item1);

        trackingService.recordTracking(
                EntityType.EWASTE_ITEM,
                savedItem1.getId(),
                EWasteStatus.SUBMITTED.name(),
                alice,
                "Device submitted by consumer for drop-off at Downtown Public Drop Box A"
        );

        EWasteItem item2 = new EWasteItemBuilder()
                .consumer(bob)
                .category(smartphone)
                .dropOffPoint(dropPointB)
                .deviceDescription("Samsung Galaxy S20 with shattered front glass; internal circuitry and battery operational")
                .status(EWasteStatus.SUBMITTED)
                .submittedAt(LocalDateTime.now().minusHours(1))
                .build();
        EWasteItem savedItem2 = ewasteItemRepository.save(item2);

        trackingService.recordTracking(
                EntityType.EWASTE_ITEM,
                savedItem2.getId(),
                EWasteStatus.SUBMITTED.name(),
                bob,
                "Device submitted by consumer for intake at GreenTech Intake Bay B"
        );

        logger.info("Seed data successfully populated! Demo accounts ready:");
        logger.info("  Admin:    admin@ewaste.org / admin123");
        logger.info("  Staff:    staff@greentech.org / staff123");
        logger.info("  Consumer: alice@example.com / pass123, bob@example.com / pass123");
        logger.info("  Business: contact@circularelectronics.com / biz123");
    }
}
