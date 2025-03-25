# Connect hard macro pins explicitly before PDN generation
globalNetConnect vccd1 -type pgpin -pin VPWR -inst_pattern *lane0*
globalNetConnect vccd1 -type pgpin -pin VPWR -inst_pattern *lane1*
globalNetConnect vssd1 -type pgpin -pin VGND -inst_pattern *lane0*
globalNetConnect vssd1 -type pgpin -pin VGND -inst_pattern *lane1*