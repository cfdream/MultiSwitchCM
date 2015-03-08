function diffNumPktsToSendSignal_vs_AccuracyFN()

    data=load('C:\workspace\projects\eclipse\MultiSwitchCM\data\diffNumPktsToSendSignal_vs_AccuracyFN\diffNumPktsToSendSignal_vs_AccuracyFN.txt');

    %size
    sizeData=size(data);
    rows=sizeData(1);

    %one tenth memory
    tenthMem=data(1,2);
    
    accuracy(1,1,1)=1;
    accuracySD(1,1,1)=1;
    fn(1,1,1)=1;
    fnSD(1,1,1)=1;
    for i=1:rows
        ithMem=ithMemory(tenthMem, data(i,2));
        accuracy(ithMem, log10(data(i,7))-2)=data(i,3);
        accuracySD(ithMem, log10(data(i,7))-2)=data(i,4);
        fn(ithMem, log10(data(i,7))-2)=data(i,5);
        fnSD(ithMem, log10(data(i,7))-2)=data(i,6);
    end
    
    %accuracy
    figure
    hold on;    
    errorbar(1:4, accuracy(1,:), accuracySD(1,:), '-rv', 'linewidth', 1.5);
    errorbar(1:4, accuracy(2,:), accuracySD(2,:), '-g>', 'linewidth', 1.5);
    errorbar(1:4, accuracy(3,:), accuracySD(3,:), '-b^', 'linewidth', 1.5);
    errorbar(1:4, accuracy(4,:), accuracySD(4,:), '-m+', 'linewidth', 1.5);
    errorbar(1:4, accuracy(5,:), accuracySD(5,:), '-co', 'linewidth', 1.5);

    set(gca, 'FontSize', 18,'XTick', 1:4, 'xticklabel', {1000, 10000, 100000, 1000000});
    legend('#buckets=20604', '#buckets=51510', '#buckets=103020', '#buckets=154530',  '#buckets=206040');
    legend('Location', 'southwest');
    legend('boxoff');
    xlabel('Send Condition when receiving #pkts')
    ylabel('accuracy')
    xlim([0.95,4.05])
    ylim([0.70,0.95])
    hold off;
    
    %false negative
    figure
    hold on;    
    errorbar(1:4, fn(1,:), fnSD(1,:), '-rv', 'linewidth', 1.5);
    errorbar(1:4, fn(2,:), fnSD(2,:), '-g>', 'linewidth', 1.5);
    errorbar(1:4, fn(3,:), fnSD(3,:), '-b^', 'linewidth', 1.5);
    errorbar(1:4, fn(4,:), fnSD(4,:), '-m+', 'linewidth', 1.5);
    errorbar(1:4, fn(5,:), fnSD(5,:), '-co', 'linewidth', 1.5);

    set(gca, 'FontSize', 18,'XTick', 1:4, 'xticklabel', {1000, 10000, 100000, 1000000});
    legend('#buckets=20604', '#buckets=51510', '#buckets=103020', '#buckets=154530',  '#buckets=206040');
    legend('Location', 'northwest');
    legend('boxoff');
    xlabel('Send Condition when receiving #pkts')
    ylabel('false negative')
    xlim([0.95,4.05])
    %ylim([0.70,0.95])
    hold off;
end

function idx=ithMemory(tenthMem, mem)
    if tenthMem == mem
        idx=1;
        return;
    end
    times=mem/tenthMem;
    idx=int16(times/2.5)+1;
end
